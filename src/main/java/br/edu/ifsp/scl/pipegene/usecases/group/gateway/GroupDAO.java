package br.edu.ifsp.scl.pipegene.usecases.group.gateway;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;

import java.util.Collection;
import java.util.Optional;
import java.util.UUID;

public interface GroupDAO {

    Group saveGroup(Group group);

    GroupParticipation saveGroupParticipation(GroupParticipation groupParticipation);

    void updateGroupParticipation(GroupParticipation groupParticipation);

    Optional<GroupParticipation> findGroupParticipationById(UUID groupParticipationId);

    Optional<Group> findGroupById(UUID groupId);

    GroupParticipation deleteGroupParticipation(UUID id);

}
