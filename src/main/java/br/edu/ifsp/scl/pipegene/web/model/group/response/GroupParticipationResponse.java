package br.edu.ifsp.scl.pipegene.web.model.group.response;

import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;

import java.sql.Timestamp;
import java.util.UUID;

public class GroupParticipationResponse {

    private UUID id;
    private GroupParticipationStatusEnum status;
    private Timestamp createdDate;

    private GroupParticipationResponse(UUID id, GroupParticipationStatusEnum status, Timestamp createdDate) {
        this.id = id;
        this.status = status;
        this.createdDate = createdDate;
    }

    public static GroupParticipationResponse createFromGroupParticipation(GroupParticipation groupParticipation) {
        return new GroupParticipationResponse(groupParticipation.getId(), groupParticipation.getStatus(), groupParticipation.getCreatedDate());
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

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
}
