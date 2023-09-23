package br.edu.ifsp.scl.pipegene.usecases.group;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;
import br.edu.ifsp.scl.pipegene.usecases.account.gateway.UserApplicationDAO;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import org.springframework.dao.PermissionDeniedDataAccessException;
import org.springframework.stereotype.Service;

import java.sql.Timestamp;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import static java.time.Instant.now;

@Service
public class GroupCRUDimpl implements GroupCRUD{

    private final GroupDAO groupDAO;
    private final UserApplicationDAO userApplicationDAO;
    private final IAuthenticationFacade authentication;

    public GroupCRUDimpl(GroupDAO groupDAO, UserApplicationDAO userApplicationDAO, IAuthenticationFacade authentication) {
        this.groupDAO = groupDAO;
        this.userApplicationDAO = userApplicationDAO;
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

        if (userId != groupParticipation.getSubmitterId())
            throw new PermissionDeniedDataAccessException("You don't have permission to deny this group participation", null);

        return groupDAO.deleteGroupParticipation(groupParticipation.getId());
    }

    @Override
    public Group findGroupByProjectId(UUID projectId) {
        return groupDAO.findGroupByProjectId(projectId)
                .orElseThrow(() -> new ResourceNotFoundException("Not found group with project id: " + projectId));
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
