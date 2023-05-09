package br.edu.ifsp.scl.pipegene.domain;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Group {

    private UUID id;
    private String name;
    private ArrayList<GroupParticipation> groupParticipations;
    private String description;
    private UUID ownerId;

    private Group(UUID id, String name, ArrayList<GroupParticipation> members, String description, UUID ownerId) {
        this.id = id;
        this.name = name;
        this.groupParticipations = members;
        this.description = description;
        this.ownerId = ownerId;
    }

    private Group(UUID id) {
        this.id = id;
    }

    public static Group createWithOnlyId(UUID id) {
        return new Group(id);
    }

    public static Group createWithMembersAndInvitedUsers(UUID id, String name, String description, UUID ownerId, ArrayList<GroupParticipation> groupParticipations) {
        ArrayList<GroupParticipation> groupParticipationList = Objects.isNull(groupParticipations) ? new ArrayList<>() : new ArrayList<>(groupParticipations);
        return new Group(id, name, groupParticipationList, description, ownerId);
    }

    public static Group createWithoutGroupParticipations(UUID id, String name, String description, UUID ownerId) {
        return new Group(id, name, new ArrayList<>(), description, ownerId);
    }

    public void addParticipationArrayList(ArrayList<GroupParticipation> groupParticipations) {
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
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
