package br.edu.ifsp.scl.pipegene.usecases.project;

import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.Project;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.web.model.account.request.CreateUserRequest;
import br.edu.ifsp.scl.pipegene.web.model.project.ProjectUpdateRequest;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

public interface ProjectCRUD {

    Project createNewProject(String name, String description, List<MultipartFile> files, List<String> applicationUserList);
    Project findProjectById(UUID projectId);
    Project findProjectByPipelineId(UUID pipelineId);
    Project updateProjectById(UUID projectId, ProjectUpdateRequest request);
    List<Project> findAllProjects();
    List<ApplicationUser> findAllUsersByProjectId(UUID projectId);
    List<ApplicationUser> getAllUsersWithAcceptedStatusByProjectId(UUID projectId);
    Project findProjectByGroupParticipationId(UUID groupParticipationId);
    void deleteProjectById(UUID projectId);
    void deleteAllUsersParticipationByProjectId(UUID projectId, List<UUID> usersIds);
    boolean isOwner(UUID projectId);
}
