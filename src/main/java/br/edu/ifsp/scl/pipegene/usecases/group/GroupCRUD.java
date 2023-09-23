package br.edu.ifsp.scl.pipegene.usecases.group;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;

import java.util.List;
import java.util.UUID;

public interface GroupCRUD {

    Group addNewGroup();
    Group findGroupById(UUID id);
    List<Group> findAllGroupByUserId();
    GroupParticipation addToGroup(UUID groupId, String username);
    GroupParticipation acceptGroupParticipation(UUID groupParticipationId);
    GroupParticipation denyGroupParticipation(UUID groupParticipationId);
    GroupParticipation exitGroup(UUID groupParticipationId);
    GroupParticipation deleteGroupParticipation(UUID groupParticipationId);

    Group findGroupByProjectId(UUID projectId);

}
