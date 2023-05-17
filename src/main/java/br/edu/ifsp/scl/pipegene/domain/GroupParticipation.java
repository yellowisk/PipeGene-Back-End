package br.edu.ifsp.scl.pipegene.domain;

import java.util.UUID;

public class GroupParticipation {

    private UUID id;
    private Group group;
    private UUID receiverId;
    private GroupParticipationStatusEnum status;
    private UUID submitterId;

    private GroupParticipation(UUID id, Group group, UUID receiverId, GroupParticipationStatusEnum status, UUID submitterId) {
        this.id = id;
        this.group = group;
        this.receiverId = receiverId;
        this.status = status;
        this.submitterId = submitterId;
    }

    private GroupParticipation(UUID id) {
        this.id = id;
    }

    public static GroupParticipation createOnlyWithId(UUID id){
        return new GroupParticipation(id);
    }
    public static GroupParticipation createWithAllFields(UUID id, Group group, UUID receiverId, GroupParticipationStatusEnum status, UUID submitterId){
        return new GroupParticipation(id, group, receiverId, status, submitterId);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Group getGroup() {
        return group;
    }

    public void setGroup(Group group) {
        this.group = group;
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
