package br.edu.ifsp.scl.pipegene.usecases.group.gateway;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;

import java.util.Collection;
import java.util.UUID;

public interface GroupDAO {

    Group saveGroup(Group group);

    GroupParticipation saveGroupParticipation(GroupParticipation groupParticipation);

    void acceptGroupParticipation(GroupParticipation groupParticipation);

    void rejectGroupParticipation(GroupParticipation groupParticipation);

    void leaveGroup(GroupParticipation groupParticipation);

    Collection<Group> findAllGroupsByUserId(UUID userId);

    Collection<GroupParticipation> findAllGroupParticipationsByGroupId(UUID groupId);


}
