package br.edu.ifsp.scl.pipegene.web.model.group.response;

import br.edu.ifsp.scl.pipegene.domain.Group;

import java.util.UUID;

public class GroupResponse {

    private UUID id;

    public GroupResponse(UUID id) {
        this.id = id;
    }
    public static GroupResponse createFromGroup(Group group) {
        return new GroupResponse(group.getId());
    }

    public UUID getId() {
        return id;
    }

}
