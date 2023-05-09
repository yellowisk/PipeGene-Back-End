package br.edu.ifsp.scl.pipegene.usecases.group;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;

import java.util.UUID;

public interface GroupCRUD {

    Group addNewGroup(String name, String description);

    GroupParticipation addToGroup(UUID groupId, String username);

    GroupParticipation acceptGroupParticipation(UUID groupParticipationId);

    GroupParticipation denyGroupParticipation(UUID groupParticipationId);

    GroupParticipation exitGroup(UUID groupParticipationId);

}
