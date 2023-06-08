package br.edu.ifsp.scl.pipegene.domain;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public class Group {

    private UUID id;
    private ArrayList<GroupParticipation> groupParticipations;
    private UUID ownerId;

    private Group(UUID id, ArrayList<GroupParticipation> members, UUID ownerId) {
        this.id = id;
        this.groupParticipations = members;
        this.ownerId = ownerId;
    }

    private Group(UUID id) {
        this.id = id;
    }

    public static Group createWithOnlyId(UUID id) {
        return new Group(id);
    }

    public static Group createWithMembersAndInvitedUsers(UUID id, UUID ownerId, ArrayList<GroupParticipation> groupParticipations) {
        ArrayList<GroupParticipation> groupParticipationList = Objects.isNull(groupParticipations) ? new ArrayList<>() : new ArrayList<>(groupParticipations);
        return new Group(id, groupParticipationList, ownerId);
    }

    public static Group createWithoutGroupParticipations(UUID id, UUID ownerId) {
        return new Group(id, new ArrayList<>(), ownerId);
    }

    public void addParticipationList(List<GroupParticipation> groupParticipations) {
        this.groupParticipations.addAll(groupParticipations);
    }

    public void addParticipation(GroupParticipation groupParticipation) {
        this.groupParticipations.add(groupParticipation);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public UUID getOwnerId() {
        return ownerId;
    }

    public void setOwnerId(UUID ownerId) {
        this.ownerId = ownerId;
    }
    public ArrayList<GroupParticipation> getGroupParticipations() {
        return groupParticipations;
    }
}
