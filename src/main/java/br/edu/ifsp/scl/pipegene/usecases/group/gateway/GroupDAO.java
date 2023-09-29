package br.edu.ifsp.scl.pipegene.usecases.group.gateway;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;

import javax.swing.text.html.Option;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface GroupDAO {

    Group saveGroup(Group group);
    GroupParticipation saveGroupParticipation(GroupParticipation groupParticipation);

    void updateGroupParticipation(GroupParticipation groupParticipation);

    Optional<GroupParticipation> findGroupParticipationById(UUID groupParticipationId);

    Optional<Group> findGroupById(UUID groupId);

    List<Group> findAllGroupByUserId(UUID userId);

    GroupParticipation deleteGroupParticipation(UUID id);
    Optional<GroupParticipation> findGroupParticipationByGroupIdAndReceiverId(UUID groupId, UUID receiverId);

    Optional<Group> findGroupByProjectId(UUID projectId);

    List<GroupParticipation> findAllGroupParticipationByGroupId(UUID groupId);

}
