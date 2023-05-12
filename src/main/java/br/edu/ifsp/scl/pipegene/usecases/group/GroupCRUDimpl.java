package br.edu.ifsp.scl.pipegene.usecases.group;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;
import br.edu.ifsp.scl.pipegene.usecases.account.gateway.UserApplicationDAO;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

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
    public Group addNewGroup(String name, String description) {
        Group group = Group.createWithoutGroupParticipations(UUID.randomUUID(), name, description, authentication.getUserAuthenticatedId());
        return groupDAO.saveGroup(group);
    }

    @Override
    public GroupParticipation addToGroup(UUID groupId, String username) {
        Optional<ApplicationUser> userOptional = userApplicationDAO.findUserByUsername(username);
        if (userOptional.isEmpty())
            throw new ResourceNotFoundException("Not found user with username: " + username);

        Optional<Group> groupOptional = groupDAO.findGroupById(groupId);
        if (groupOptional.isEmpty())
            throw new ResourceNotFoundException("Not found group with id: " + groupId);

        var groupParticipation = GroupParticipation.createWithAllFields(UUID.randomUUID(), groupOptional.get(), userOptional.get().getId(), GroupParticipationStatusEnum.PENDING, authentication.getUserAuthenticatedId());
        return groupDAO.saveGroupParticipation(groupParticipation);
    }

    @Override
    public GroupParticipation acceptGroupParticipation(UUID groupParticipationId) {
        var groupParticipation = getParticionOrThrow(groupParticipationId);
        groupParticipation.setStatus(GroupParticipationStatusEnum.ACCEPTED);
        groupDAO.updateGroupParticipation(groupParticipation);
        return groupParticipation;
    }

   /* public GroupParticipation acceptGroupParticipation(UUID groupParticipationId) {
        return updateParticipationStatus(getParticionOrThrow(groupParticipationId), () ->
            updateParticipationStatus(groupParticipationId, )
        );
    }*/

    /*private GroupParticipation updateParticipationStatus(GroupParticipation groupParticipation, Runnable newStatus){
        newStatus.run();
        groupDAO.updateGroupParticipation(groupParticipation);
        return groupParticipation;
    }*/

    @Override
    public GroupParticipation denyGroupParticipation(UUID groupParticipationId) {
        var groupParticipation = getParticionOrThrow(groupParticipationId);
        groupParticipation.setStatus(GroupParticipationStatusEnum.REJECTED);
        groupDAO.updateGroupParticipation(groupParticipation);
        return groupParticipation;
    }

    @Override
    public GroupParticipation exitGroup(UUID groupParticipationId){
        GroupParticipation groupParticipation = getParticionOrThrow(groupParticipationId);
        groupParticipation.setStatus(GroupParticipationStatusEnum.EXITED);
        groupDAO.updateGroupParticipation(groupParticipation);
        return groupParticipation;
    }

    private GroupParticipation getParticionOrThrow(UUID groupParticipationId) {
        Optional<GroupParticipation> groupParticipationOptional = groupDAO.findGroupParticipationById(groupParticipationId);
        if (groupParticipationOptional.isEmpty())
            throw new ResourceNotFoundException("Not found group participation with id: " + groupParticipationId);

        var groupParticipation = groupParticipationOptional.get();
        return groupParticipation;
    }
}
