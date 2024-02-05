package br.edu.ifsp.scl.pipegene.usecases.provider;

import br.edu.ifsp.scl.pipegene.configuration.security.AuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.Project;
import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.usecases.account.gateway.UserApplicationDAO;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.group.GroupCRUD;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import br.edu.ifsp.scl.pipegene.usecases.project.ProjectCRUD;
import br.edu.ifsp.scl.pipegene.usecases.provider.gateway.ProviderDAO;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import br.edu.ifsp.scl.pipegene.web.model.provider.request.ProviderRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProviderServiceImpl implements ProviderService {

    private final ProviderDAO providerDAO;

    private final UserApplicationDAO userApplicationDAO;

    private final GroupDAO groupDAO;

    private final GroupCRUD groupCRUD;
    private final ProjectCRUD projectCRUD;

    private final AuthenticationFacade authenticationFacade;

    public ProviderServiceImpl(ProviderDAO providerDAO, UserApplicationDAO userApplicationDAO, GroupDAO groupDAO, GroupCRUD groupCRUD, ProjectCRUD projectCRUD, AuthenticationFacade authenticationFacade) {
        this.providerDAO = providerDAO;
        this.userApplicationDAO = userApplicationDAO;
        this.groupDAO = groupDAO;
        this.groupCRUD = groupCRUD;
        this.projectCRUD = projectCRUD;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public List<Provider> listAllProviders() {
        return providerDAO.findAllProviders();
    }

    @Override
    public Provider createNewProvider(ProviderRequest providerRequest) {
        Provider provider = providerRequest.convertToProvider();
        UUID providerId = UUID.randomUUID();
        provider.setId(providerId);

        providerDAO.saveNewProvider(provider);


        if (providerRequest.getSelectedProjectIds() != null) {
            providerRequest.getSelectedProjectIds().forEach(projectId -> {
                this.insertIntoGroup(groupCRUD.findGroupByProjectId(projectId).getId(), providerId);
            });
        }
        return provider;
    }

    @Override
    public List<UUID> findProjectsIdByProviderId(UUID providerId) {
        List<UUID> groupsId = providerDAO.findAllGroupsByProviderId(providerId);
        List<UUID> projectsId = groupsId.stream()
                .map(projectCRUD::findProjectByGroupId)
                .map(Project::getId)
                .collect(Collectors.toList());
        return projectsId;
    }

    @Override
    public List<Provider> findProjectsByProjectIdAndUserId(UUID projectId) {
        return providerDAO.findAllProvidersByUserAndProjectId(authenticationFacade.getUserAuthenticatedId(), projectId);
    }

    @Override
    public void insertIntoGroup(UUID groupId, UUID providerId) {
        groupDAO.findGroupById(groupId).orElseThrow(
                () -> new ResourceNotFoundException("Not found group with id: " + groupId)
        );
        providerDAO.findProviderById(providerId).orElseThrow(
                () -> new ResourceNotFoundException("Not found provider with id: " + providerId)
        );
        if (providerDAO.existsGroupProvider(groupId, providerId)) {
            throw new IllegalArgumentException("Provider already exists in group");
        }

        providerDAO.createGroupProvider(groupId, providerId);
    }

    @Override
    public Provider updateProvider(UUID providerId, ProviderRequest providerRequest) {
        Optional<Provider> optional = providerDAO.findProviderById(providerId);

        if(optional.isEmpty()) {
            throw new ResourceNotFoundException("Not found provider with id: " + providerId);
        }

        Provider provider = providerRequest.convertToProvider();
        System.out.println(provider);

        return providerDAO.updateProvider(provider.getNewInstanceWithId(providerId));
    }

    @Override
    public List<Provider> listAllProvidersByUserId() {
        UUID userId = authenticationFacade.getUserAuthenticatedId();

        Optional<ApplicationUser> optionalUser = userApplicationDAO.findUserById(userId);

        if (optionalUser.isEmpty()){
            throw new ResourceNotFoundException("Not found user with id: " + userId);
        }

        return providerDAO.findAllProvidersByUserId(userId);
    }

    @Override
    public boolean isOwner(UUID providerId) {
        UUID userId = authenticationFacade.getUserAuthenticatedId();

        Optional<ApplicationUser> optionalUser = userApplicationDAO.findUserById(userId);

        if (optionalUser.isEmpty()){
            throw new ResourceNotFoundException("Not found user with id: " + userId);
        }

        return providerDAO.isOwner(providerId, userId);
    }

    @Override
    public Provider findProviderById(UUID providerId) {
        Optional<Provider> optional = providerDAO.findProviderById(providerId);

        if(optional.isEmpty()) {
            throw new ResourceNotFoundException("Not found provider with id: " + providerId);
        }

        return optional.get();
    }
}
