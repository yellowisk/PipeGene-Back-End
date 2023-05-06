package br.edu.ifsp.scl.pipegene.web.model.group.response;

import br.edu.ifsp.scl.pipegene.domain.Group;

import java.util.UUID;

public class GroupResponse {

    private UUID id;
    private String name;
    private String description;

    public GroupResponse(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }
    public static GroupResponse createFromGroup(Group group) {
        return new GroupResponse(group.getId(), group.getName(), group.getDescription());
    }
}
