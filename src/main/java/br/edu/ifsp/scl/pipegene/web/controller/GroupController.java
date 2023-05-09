package br.edu.ifsp.scl.pipegene.web.controller;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.usecases.group.GroupCRUD;
import br.edu.ifsp.scl.pipegene.web.model.group.response.GroupParticipationResponse;
import br.edu.ifsp.scl.pipegene.web.model.group.response.GroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequestMapping("api/v1/groups/")
@RestController
public class GroupController {

    private final GroupCRUD groupCRUD;

    public GroupController(GroupCRUD groupCRUD) {
        this.groupCRUD = groupCRUD;
    }

    @PostMapping("/save")
    public ResponseEntity<GroupResponse> addNewGroup(@RequestParam("name") String name,
                                                     @RequestParam("description") String description) {
        Group group = groupCRUD.addNewGroup(name, description);
        return ResponseEntity.ok(GroupResponse.createFromGroup(group));
    }

    @PostMapping("/addUser")
    public ResponseEntity<GroupParticipationResponse> addToGroup(@RequestParam("groupId") UUID groupId,
                                                         @RequestParam("username") String username) {
        GroupParticipation groupParticipation = groupCRUD.addToGroup(groupId, username);
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }

    @PatchMapping("/acceptParticipation")
    public ResponseEntity<GroupParticipationResponse> acceptGroupParticipation(@RequestParam("id")
                                                                                   UUID groupParticipationId){
        GroupParticipation groupParticipation = groupCRUD.acceptGroupParticipation(groupParticipationId);
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }

    @PatchMapping("/denyParticipation")
    public ResponseEntity<GroupParticipationResponse> denyGroupParticipation(@RequestParam("id")
                                                                               UUID groupParticipationId){
        GroupParticipation groupParticipation = groupCRUD.denyGroupParticipation(groupParticipationId);
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }

    @PatchMapping("/leave")
    public ResponseEntity<GroupParticipationResponse> leaveGroup(@RequestParam("id") UUID groupParticipationId){
        GroupParticipation groupParticipation = groupCRUD.exitGroup(groupParticipationId);
        return ResponseEntity.ok(GroupParticipationResponse.createFromGroupParticipation(groupParticipation));
    }

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Teste");
    }
}
