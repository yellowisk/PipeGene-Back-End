package br.edu.ifsp.scl.pipegene.web.model.provider.request;

import java.util.UUID;

public class GroupProvider {

    private UUID groupId;
    private UUID providerId;

    public GroupProvider() {
    }

    public GroupProvider(UUID groupId, UUID providerId) {
        this.groupId = groupId;
        this.providerId = providerId;
    }

    public UUID getGroupId() {
        return groupId;
    }

    public void setGroupId(UUID groupId) {
        this.groupId = groupId;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }
}
