package br.edu.ifsp.scl.pipegene.web.model.group.request;

import javax.validation.constraints.NotNull;

public class CreateGroupRequest {

    @NotNull
    private String name;
    @NotNull
    private String description;

    public CreateGroupRequest() {
    }

    public CreateGroupRequest(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
