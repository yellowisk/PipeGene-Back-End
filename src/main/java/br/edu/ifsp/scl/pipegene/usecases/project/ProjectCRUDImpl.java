package br.edu.ifsp.scl.pipegene.usecases.project;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.Dataset;
import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.Project;
import br.edu.ifsp.scl.pipegene.usecases.account.ApplicationUserCRUD;
import br.edu.ifsp.scl.pipegene.usecases.account.ApplicationUserService;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.group.GroupCRUD;
import br.edu.ifsp.scl.pipegene.usecases.project.gateway.ObjectStorageService;
import br.edu.ifsp.scl.pipegene.usecases.project.gateway.ProjectDAO;
import br.edu.ifsp.scl.pipegene.web.exception.GenericResourceException;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceForbiddenException;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import br.edu.ifsp.scl.pipegene.web.model.account.request.CreateUserRequest;
import br.edu.ifsp.scl.pipegene.web.model.project.ProjectUpdateRequest;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ProjectCRUDImpl implements ProjectCRUD {

    private final ProjectDAO projectDAO;
    private final ObjectStorageService objectStorageService;
    private final IAuthenticationFacade authentication;
    private final GroupCRUD groupCRUD;

    private final ApplicationUserService applicationUserService;

    public ProjectCRUDImpl(ProjectDAO projectDAO, ObjectStorageService objectStorageService, IAuthenticationFacade authentication, GroupCRUD groupCRUD, ApplicationUserService applicationUserService) {
        this.projectDAO = projectDAO;
        this.objectStorageService = objectStorageService;
        this.authentication = authentication;
        this.groupCRUD = groupCRUD;
        this.applicationUserService = applicationUserService;
    }

    @Override
    public Project createNewProject(String name, String description, List<MultipartFile> files, List<String> usersUsername) {
        List<Dataset> datasets = files.stream()
                .map(objectStorageService::putObject)
                .collect(Collectors.toList());
        Group group = groupCRUD.addNewGroup();
        if (usersUsername != null)
            usersUsername.forEach(username -> groupCRUD.addToGroup(group.getId(), username));

        return projectDAO.saveNewProject(name, description, group.getId(), datasets, authentication.getUserAuthenticatedId());
    }

    @Override
    public Project findProjectById(UUID projectId) {
        Optional<Project> optional = projectDAO.findProjectById(projectId);

        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }

        Project project = optional.get();
        verifyAccess(project.getOwnerId());

        return project;
    }

    @Override
    public Project findProjectByPipelineId(UUID pipelineId) {
        Optional<Project> optional = projectDAO.findByPipelineId(pipelineId);

        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("Not found project with pipeline id: " + pipelineId);
        }

        Project project = optional.get();
        verifyAccess(project.getOwnerId());

        return project;
    }

    @Override
    public Project updateProjectById(UUID projectId, ProjectUpdateRequest request) {
        Optional<Project> optional = projectDAO.findProjectById(projectId);

        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }

        Group group = groupCRUD.findGroupByProjectId(projectId);
        List<GroupParticipation> groupParticipationList = groupCRUD.getAllGroupParticipationsByGroupId(group.getId());
        List<UUID> usersIds = groupParticipationList.stream().map(GroupParticipation::getReceiverId).collect(Collectors.toList());
        List<UUID> requestUsersId = request.getUsersId();
        List<UUID> newUsersIds;
        List<UUID> deleteUsersIds;

        newUsersIds = requestUsersId.stream().filter(userId -> !usersIds.contains(userId)).collect(Collectors.toList());
        deleteUsersIds = usersIds.stream().filter(userId -> !requestUsersId.contains(userId)).collect(Collectors.toList());

        deleteUsersIds.stream().forEach(userid -> {
            if (!userid.equals(authentication.getUserAuthenticatedId())){
                GroupParticipation gp = groupCRUD.findGroupParticipationByGroupAndReceiverId(group.getId(), userid);
                groupCRUD.deleteGroupParticipation(gp.getId());
            }
        });


        newUsersIds.stream().forEach(userid -> {
            groupCRUD.addToGroup(group.getId(), applicationUserService.findUserById(userid).getUsername());
        });

        Project project = optional.get();
        verifyAccess(project.getOwnerId());
        return projectDAO.updateProject(
                project.getUpdatedInstance(request)
        );
    }

    @Override
    public List<Project> findAllProjects() {
        return projectDAO.findAllProjectsByUser(authentication.getUserAuthenticatedId());
    }

    @Override
    public List<ApplicationUser> findAllUsersByProjectId(UUID groupId) {
        return applicationUserService.getUsersByGroupId(groupId);
    }


    @Override
    public void deleteProjectById(UUID projectId) {
        Optional<Project> optional = projectDAO.findProjectById(projectId);

        if (optional.isEmpty()) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }
        Project project = optional.get();
        verifyAccess(project.getOwnerId());

        if (!projectDAO.deleteProjectById(projectId)) {
            throw new GenericResourceException("The project cannot be deleted because exists process still using him", "Delete project not allowed");
        }

    }

    @Override
    public void deleteAllUsersParticipationByProjectId(UUID projectId, List<UUID> usersIds) {
        Group group = groupCRUD.findGroupByProjectId(projectId);
        List<GroupParticipation> groupParticipationList = usersIds
                .stream().map(userId -> groupCRUD.findGroupParticipationByGroupAndReceiverId(group.getId(), userId))
                .collect(Collectors.toList());
        groupParticipationList.forEach(groupParticipation -> groupCRUD.deleteGroupParticipation(groupParticipation.getId()));
    }

    private void verifyAccess(UUID projectOwnerId) {
        if (!projectOwnerId.equals(authentication.getUserAuthenticatedId())) {
            throw new ResourceForbiddenException("You don't have access for this resource");
        }
    }
}
