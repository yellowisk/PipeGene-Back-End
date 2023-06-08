package br.edu.ifsp.scl.pipegene.web.model.account.request;

import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;

import javax.validation.constraints.NotNull;
import java.util.UUID;

public class UserRequest {
    @NotNull
    private String username;
    @NotNull
    private String name;
    @NotNull
    private String orcid;
    @NotNull
    private String github;

    public UserRequest() {
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getOrcid() {
        return orcid;
    }

    public void setOrcid(String orcid) {
        this.orcid = orcid;
    }

    public String getGithub() {
        return github;
    }

    public void setGithub(String github) {
        this.github = github;
    }

    public ApplicationUser toApplicationUser() {
        return new ApplicationUser(username, name, orcid, github);
    }
}
