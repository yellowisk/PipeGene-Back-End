package br.edu.ifsp.scl.pipegene.usecases.group;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;
import br.edu.ifsp.scl.pipegene.domain.Project;
import br.edu.ifsp.scl.pipegene.usecases.account.gateway.UserApplicationDAO;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import br.edu.ifsp.scl.pipegene.usecases.project.gateway.ProjectDAO;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import br.edu.ifsp.scl.pipegene.web.model.group.response.GroupParticipationView;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Collectors;

import static java.time.Instant.now;

@Service
public class GroupCRUDimpl implements GroupCRUD{

    private final GroupDAO groupDAO;
    private final UserApplicationDAO userApplicationDAO;

    private final ProjectDAO projectDAO;
    private final IAuthenticationFacade authentication;

    public GroupCRUDimpl(GroupDAO groupDAO, UserApplicationDAO userApplicationDAO, ProjectDAO projectDAO, IAuthenticationFacade authentication) {
        this.groupDAO = groupDAO;
        this.userApplicationDAO = userApplicationDAO;
        this.projectDAO = projectDAO;
        this.authentication = authentication;
    }

    @Override
    public Group addNewGroup() {
        UUID groupId = UUID.randomUUID();
        Group group = Group.createWithoutGroupParticipations(groupId, authentication.getUserAuthenticatedId());
        GroupParticipation groupParticipation = GroupParticipation.createWithGroupCreation(
                UUID.randomUUID(), groupId, authentication.getUserAuthenticatedId(), Timestamp.from(now())
        );
        groupDAO.saveGroup(group);
        groupDAO.saveGroupParticipation(groupParticipation);
        return Group.createWithOnlyId(groupId);
    }

    @Override
    public Group findGroupById(UUID id) {
        return groupDAO.findGroupById(id).orElseThrow(
                () -> new ResourceNotFoundException("Not found group with id: " + id)
        );
    }

    @Override
    public List<Group> findAllGroupByUserId() {
        return groupDAO.findAllGroupByUserId(authentication.getUserAuthenticatedId());
    }

    @Override
    public List<GroupParticipation> getAllGroupParticipationsByGroupId(UUID groupId) {
        return groupDAO.findAllGroupParticipationByGroupId(groupId);
    }

    @Override
    public GroupParticipation addToGroup(UUID groupId, String username) {
        GroupParticipation groupParticipation = null;

        ApplicationUser applicationUser = userApplicationDAO.findUserByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("Not found user with username: " + username));

        groupDAO.findGroupById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found group with id: " + groupId));

        Optional<GroupParticipation> groupParticipationOptional = groupDAO.findGroupParticipationByGroupIdAndReceiverId(groupId, applicationUser.getId());
        if (groupParticipationOptional.isPresent()){
            groupParticipation = groupParticipationOptional.get();
            if (groupParticipation.getStatus().equals(GroupParticipationStatusEnum.ACCEPTED))
                throw new PermissionDeniedDataAccessException("User already in group", null);
            if (groupParticipation.getStatus().equals(GroupParticipationStatusEnum.PENDING))
                throw new PermissionDeniedDataAccessException("User already invited to the group", null);
            groupParticipation.setStatus(GroupParticipationStatusEnum.PENDING);
            groupParticipation.setCreatedDate(Timestamp.from(now()));
            groupDAO.updateGroupParticipation(groupParticipation);
            return groupParticipation;
        }
        groupParticipation = GroupParticipation.createWithAllFields(UUID.randomUUID(), groupId, applicationUser.getId(), GroupParticipationStatusEnum.PENDING, authentication.getUserAuthenticatedId(), Timestamp.from(now()));
        return groupDAO.saveGroupParticipation(groupParticipation);
    }

    @Override
    public GroupParticipation acceptGroupParticipation(UUID groupParticipationId) {
        return updateGroupStatus(getParticionOrThrow(groupParticipationId),
                GroupParticipation::acceptGroup);
    }

    @Override
    public GroupParticipation denyGroupParticipation(UUID groupParticipationId) {
        return updateGroupStatus(getParticionOrThrow(groupParticipationId),
                GroupParticipation::denyGroup);
    }

    @Override
    public GroupParticipation exitGroup(UUID groupParticipationId){
        return updateGroupStatus(getParticionOrThrow(groupParticipationId),
                GroupParticipation::quitGroup);
    }

    @Override
    public GroupParticipation deleteGroupParticipation(UUID id){
        Optional<GroupParticipation> groupParticipationOptional = groupDAO.findGroupParticipationById(id);
        UUID userId = authentication.getUserAuthenticatedId();
        if (groupParticipationOptional.isEmpty())
            throw new ResourceNotFoundException("Not found group participation with id: " + id);

        var groupParticipation = groupParticipationOptional.get();

        return groupDAO.deleteGroupParticipation(groupParticipation.getId());
    }

    @Override
    public GroupParticipation findGroupParticipationByGroupAndReceiverId(UUID groupId, UUID receiverId) {
        return groupDAO.findGroupParticipationByGroupIdAndReceiverId(groupId, receiverId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found group participation with group id: " + groupId + " and receiver id: " + receiverId));
    }

    @Override
    public GroupParticipation exitGroupByProjectId(UUID projectId) {
        Group group = groupDAO.findGroupByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found group with project id: " + projectId));
        GroupParticipation groupParticipation = findGroupParticipationByGroupAndReceiverId(group.getId(), authentication.getUserAuthenticatedId());
        return updateGroupStatus(groupParticipation, GroupParticipation::quitGroup);
    }

    @Override
    public Group findGroupByProjectId(UUID projectId) {
        return groupDAO.findGroupByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found group with project id: " + projectId));
    }

    @Override
    public List<GroupParticipation> getAllGroupParticipationsWithAcceptedStatusByGroupId(UUID groupId) {
        groupDAO.findGroupById(groupId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found group with id: " + groupId));
        return groupDAO.findAllAcceptedGroupParticipationByGroupId(groupId);
    }

    @Override
    public List<GroupParticipationView> findAllGroupParticipationsByUserId() {
        List<GroupParticipation> groupParticipations = groupDAO.findAllGroupParticipationsByUserId(authentication.getUserAuthenticatedId());

        List<GroupParticipationView> participationViews = groupParticipations.stream().map((groupParticipation -> {
           Project project = projectDAO.findProjectByGroupParticipantId(groupParticipation.getId()).orElseThrow(
                     () -> new ResourceNotFoundException("Not found project with group participation id: " + groupParticipation.getId())
              );
           ApplicationUser submitterUser = userApplicationDAO.findUserById(groupParticipation.getSubmitterId()).orElseThrow(
                   () -> new ResourceNotFoundException("Not found user with id: " + groupParticipation.getSubmitterId())
           );
           return GroupParticipationView.createFromGroupParticipation(groupParticipation, submitterUser.getUsername(), project.getName());
        })).collect(Collectors.toList());
        return participationViews;
    }

    @Override
    public List<GroupParticipation> getAllPedingGroupParticipationsByReceiverId() {
        return groupDAO.findAllPendingGroupParticipationByReceiverId(authentication.getUserAuthenticatedId());
    }

    @Override
    public ApplicationUser findUserByGroupParticipationId(UUID groupParticipationId) {
        GroupParticipation groupParticipation = groupDAO.findGroupParticipationById(groupParticipationId).orElseThrow(
                () -> new ResourceNotFoundException("Not found group participation with id: " + groupParticipationId)
        );

        return userApplicationDAO.findUserById(groupParticipation.getSubmitterId())
                .orElseThrow(() -> new ResourceNotFoundException("Not found user with id: " + groupParticipation.getSubmitterId()));
    }

    private GroupParticipation getParticionOrThrow(UUID groupParticipationId) {
        Optional<GroupParticipation> groupParticipationOptional = groupDAO.findGroupParticipationById(groupParticipationId);
        if (groupParticipationOptional.isEmpty())
            throw new ResourceNotFoundException("Not found group participation with id: " + groupParticipationId);

        var groupParticipation = groupParticipationOptional.get();

        UUID userId = authentication.getUserAuthenticatedId();
        if (!userId.equals(groupParticipation.getReceiverId()))
            throw new PermissionDeniedDataAccessException("You don't have permission to deny this group participation", null);

        return groupParticipation;
    }

    private GroupParticipation updateGroupStatus(GroupParticipation group, Consumer<GroupParticipation> action){
        action.accept(group);
        groupDAO.updateGroupParticipation(group);
        return group;
    }
}
