package br.edu.ifsp.scl.pipegene.usecases.provider;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.usecases.account.gateway.UserApplicationDAO;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.group.GroupCRUD;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import br.edu.ifsp.scl.pipegene.usecases.provider.gateway.ProviderDAO;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import br.edu.ifsp.scl.pipegene.web.model.provider.request.ProviderRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ProviderServiceImpl implements ProviderService {

    private final ProviderDAO providerDAO;

    private final UserApplicationDAO userApplicationDAO;

    private final GroupDAO groupDAO;

    public ProviderServiceImpl(ProviderDAO providerDAO, UserApplicationDAO userApplicationDAO, GroupDAO groupDAO) {
        this.providerDAO = providerDAO;
        this.userApplicationDAO = userApplicationDAO;
        this.groupDAO = groupDAO;
    }

    @Override
    public List<Provider> listAllProviders() {
        return providerDAO.findAllProviders();
    }

    @Override
    public Provider createNewProvider(ProviderRequest providerRequest) {
        return providerDAO.saveNewProvider(providerRequest.convertToProvider());
    }

    @Override
    public void insertIntoGroup(UUID groupId, UUID providerId) {
        groupDAO.findGroupById(groupId).orElseThrow(
                () -> new ResourceNotFoundException("Not found group with id: " + groupId)
        );
        providerDAO.findProviderById(providerId).orElseThrow(
                () -> new ResourceNotFoundException("Not found provider with id: " + providerId)
        );
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
    public List<Provider> listAllProvidersByUserId(UUID userId) {
        Optional<ApplicationUser> optionalUser = userApplicationDAO.findUserById(userId);

        if (optionalUser.isEmpty()){
            throw new ResourceNotFoundException("Not found user with id: " + userId);
        }

        return providerDAO.findAllProvidersByUserId(userId);
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
