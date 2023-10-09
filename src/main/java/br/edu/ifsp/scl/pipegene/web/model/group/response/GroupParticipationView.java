package br.edu.ifsp.scl.pipegene.web.model.group.response;

import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;

import java.sql.Timestamp;
import java.util.UUID;

public class GroupParticipationView {

    private UUID id;
    private String submitterUsername;
    private GroupParticipationStatusEnum status;
    private String projectName;
    private Timestamp createdDate;

    public GroupParticipationView(UUID id, String submitterUsername, GroupParticipationStatusEnum status, String projectName, Timestamp createdDate) {
        this.id = id;
        this.submitterUsername = submitterUsername;
        this.status = status;
        this.projectName = projectName;
        this.createdDate = createdDate;
    }

    public static GroupParticipationView createFromGroupParticipation(GroupParticipation groupParticipation, String submitterUsername, String projectName) {
        return new GroupParticipationView(groupParticipation.getId(), submitterUsername, groupParticipation.getStatus(), projectName, groupParticipation.getCreatedDate());
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getSubmitterUsername() {
        return submitterUsername;
    }

    public void setSubmitterUsername(String submitterUsername) {
        this.submitterUsername = submitterUsername;
    }

    public GroupParticipationStatusEnum getStatus() {
        return status;
    }

    public void setStatus(GroupParticipationStatusEnum status) {
        this.status = status;
    }

    public String getProjectName() {
        return projectName;
    }

    public void setProjectName(String projectName) {
        this.projectName = projectName;
    }

    public Timestamp getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Timestamp createdDate) {
        this.createdDate = createdDate;
    }
}
