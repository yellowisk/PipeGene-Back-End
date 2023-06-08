package br.edu.ifsp.scl.pipegene.domain;

import java.sql.Timestamp;
import java.util.UUID;

public class GroupParticipation {

    private UUID id;
    private UUID groupId;
    private UUID receiverId;
    private GroupParticipationStatusEnum status;
    private UUID submitterId;
    private Timestamp createdDate;

    private GroupParticipation(UUID id, UUID groupId, UUID receiverId, GroupParticipationStatusEnum status, UUID submitterId, Timestamp createdDate) {
        this.id = id;
        this.groupId = groupId;
        this.receiverId = receiverId;
        this.status = status;
        this.submitterId = submitterId;
        this.createdDate = createdDate;
    }

    private GroupParticipation(UUID id) {
        this.id = id;
    }

    public static GroupParticipation createOnlyWithId(UUID id){
        return new GroupParticipation(id);
    }
    public static GroupParticipation createWithAllFields(UUID id, UUID groupId, UUID receiverId, GroupParticipationStatusEnum status, UUID submitterId, Timestamp createdDate){
        return new GroupParticipation(id, groupId, receiverId, status, submitterId, createdDate);
    }

    public static GroupParticipation createWithGroupCreation(UUID id, UUID groupId, UUID userId, Timestamp createdDate){
        return new GroupParticipation(id, groupId, userId, GroupParticipationStatusEnum.ACCEPTED, userId, createdDate);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getReceiverId() {
        return receiverId;
    }

    public void setReceiverId(UUID receiverId) {
        this.receiverId = receiverId;
    }

    public GroupParticipationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GroupParticipationStatusEnum status) {
        this.status = status;
    }

    public UUID getSubmitterId() {
        return submitterId;
    }

    public void setSubmitterId(UUID submitterId) {
        this.submitterId = submitterId;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }

    public void quitGroup(){
        if (!(this.status == GroupParticipationStatusEnum.ACCEPTED)){
            throw new IllegalStateException("You can't quit a group that you are not participating");
        }
        this.status = GroupParticipationStatusEnum.EXITED;
    }

    public void acceptGroup(){
        if (this.status == GroupParticipationStatusEnum.ACCEPTED){
            throw new IllegalStateException("You can't accept a group that you are already participating");
        }
        this.status = GroupParticipationStatusEnum.ACCEPTED;
    }

    public void denyGroup(){
        if (!(this.status == GroupParticipationStatusEnum.PENDING)){
            throw new IllegalStateException("You can't deny a group that you are not invited");
        }
        this.status = GroupParticipationStatusEnum.REJECTED;
    }
}
