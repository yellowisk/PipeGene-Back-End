package br.edu.ifsp.scl.pipegene.web.model.group.response;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;

import java.util.ArrayList;
import java.util.UUID;

public class GroupResponse {

    private UUID id;

    private ArrayList<GroupParticipation> groupParticipations;

    public GroupResponse(UUID id) {
        this.id = id;
    }

    public GroupResponse(UUID id, ArrayList<GroupParticipation> groupParticipations) {
        this.id = id;
        this.groupParticipations = groupParticipations;
    }

    public static GroupResponse createJustWithId(UUID id) {
        return new GroupResponse(id);
    }

    public static GroupResponse createFromGroup(Group group){
        return new GroupResponse(group.getId(), group.getGroupParticipations());
    }

    public UUID getId() {
        return id;
    }

    public ArrayList<GroupParticipation> getGroupParticipations() {
        return groupParticipations;
    }
}
