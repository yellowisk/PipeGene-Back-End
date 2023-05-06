package br.edu.ifsp.scl.pipegene.domain;

import java.util.ArrayList;
import java.util.Objects;
import java.util.UUID;

public class Group {

    private UUID id;
    private String name;
    private ArrayList<UUID> members;
    private String description;
    private UUID ownerId;
    private ArrayList<UUID> invitedUsers;

    private Group(UUID id, String name, ArrayList<UUID> members, String description, UUID ownerId, ArrayList<UUID> invitedUsers) {
        this.id = id;
        this.name = name;
        this.members = members;
        this.description = description;
        this.ownerId = ownerId;
        this.invitedUsers = invitedUsers;
    }

    private Group(UUID id) {
        this.id = id;
    }

    public static Group createWithOnlyId(UUID id) {
        return new Group(id);
    }

    public static Group createWithMembersAndInvitedUsers(UUID id, String name, String description, UUID ownerId, ArrayList<UUID> members, ArrayList<UUID> invitedUsers) {
        ArrayList<UUID> membersList = Objects.isNull(members) ? new ArrayList<>() : new ArrayList<>(members);
        ArrayList<UUID> invitedList = Objects.isNull(invitedUsers) ? new ArrayList<>() : new ArrayList<>(invitedUsers);
        return new Group(id, name, membersList, description, ownerId, invitedList);
    }

    public static Group createWithoutMembersAndInvitedUser(UUID id, String name, String description, UUID ownerId) {
        return new Group(id, name, new ArrayList<>(), description, ownerId, new ArrayList<>());
    }

    public void addMember(ArrayList<UUID> members) {
        this.members.addAll(members);
    }

    public void addInvitedUser(ArrayList<UUID> invitedUsers) {
        this.invitedUsers.addAll(invitedUsers);
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

    public ArrayList<UUID> getMembers() {
        return members;
    }

    public ArrayList<UUID> getInvitedUsers() {
        return invitedUsers;
    }
}
