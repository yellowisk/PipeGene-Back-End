package br.edu.ifsp.scl.pipegene.web.model.group.request;

import javax.validation.constraints.NotNull;
import java.util.Objects;
import java.util.UUID;

public class CreateInviteRequest {

    @NotNull
    private String username;

    @NotNull
    private UUID groupId;

    public CreateInviteRequest() {
    }

    public CreateInviteRequest(String username, UUID groupId) {
        this.username = username;
        this.groupId = groupId;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }
}
