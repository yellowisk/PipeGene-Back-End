package br.edu.ifsp.scl.pipegene.web.model.project;

import java.util.List;
import java.util.UUID;

public class ProjectUpdateRequest {

    private String name;
    private String description;

    private List<UUID> usersId;

    public ProjectUpdateRequest() {
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<UUID> getUsersId() {
        return usersId;
    }
}
