package br.edu.ifsp.scl.pipegene.web.model.group.response;

import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;

import java.util.UUID;

public class GroupParticipationResponse {

    private UUID id;
    private GroupParticipationStatusEnum status;

    private GroupParticipationResponse(UUID id, GroupParticipationStatusEnum status) {
        this.id = id;
        this.status = status;
    }

    public static GroupParticipationResponse createFromGroupParticipation(GroupParticipation groupParticipation) {
        return new GroupParticipationResponse(groupParticipation.getId(), groupParticipation.getStatus());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public GroupParticipationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GroupParticipationStatusEnum status) {
        this.status = status;
    }


}
