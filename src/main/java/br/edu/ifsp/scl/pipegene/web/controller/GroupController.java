package br.edu.ifsp.scl.pipegene.web.controller;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.usecases.group.GroupCRUD;
import br.edu.ifsp.scl.pipegene.web.model.group.request.CreateInviteRequest;
import br.edu.ifsp.scl.pipegene.web.model.group.response.GroupParticipationResponse;
import br.edu.ifsp.scl.pipegene.web.model.group.response.GroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("api/v1/groups")
public class GroupController {

    private final GroupCRUD groupCRUD;

    public GroupController(GroupCRUD groupCRUD) {
        this.groupCRUD = groupCRUD;
    }
    @PostMapping("/save")
    public ResponseEntity<GroupResponse> addNewGroup() {
        Group group = groupCRUD.addNewGroup();
        return ResponseEntity.ok(GroupResponse.createFromGroup(group));
    }

    @PostMapping("/addUser")
    public ResponseEntity<GroupParticipationResponse> addToGroup(@RequestBody CreateInviteRequest request){
        GroupParticipation groupParticipation = groupCRUD.addToGroup(request.getGroupId(), request.getUsername());
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }

    @PatchMapping("/acceptParticipation/{id}")
    public ResponseEntity<GroupParticipationResponse> acceptGroupParticipation(@PathVariable("id")
                                                                                   UUID groupParticipationId){
        GroupParticipation groupParticipation = groupCRUD.acceptGroupParticipation(groupParticipationId);
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }

    @PatchMapping("/denyParticipation/{id}")
    public ResponseEntity<GroupParticipationResponse> denyGroupParticipation(@PathVariable UUID id){
        System.out.println(id);
        GroupParticipation groupParticipation = groupCRUD.denyGroupParticipation(id);
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }

    @PatchMapping("/leaveGroup/{id}")
    public ResponseEntity<GroupParticipationResponse> leaveGroup(@PathVariable("id") UUID groupParticipationId){
        GroupParticipation groupParticipation = groupCRUD.exitGroup(groupParticipationId);
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<GroupParticipationResponse> deleteGroupParticipation(@PathVariable("id") UUID groupParticipationId){
        GroupParticipation groupParticipation = groupCRUD.deleteGroupParticipation(groupParticipationId);
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }
}
