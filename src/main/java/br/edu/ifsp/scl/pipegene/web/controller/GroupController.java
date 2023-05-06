package br.edu.ifsp.scl.pipegene.web.controller;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.usecases.group.GroupCRUD;
import br.edu.ifsp.scl.pipegene.web.model.group.response.GroupResponse;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RequestMapping("api/v1/groups/")
@RestController
public class GroupController {

    private final GroupCRUD groupCRUD;

    public GroupController(GroupCRUD groupCRUD) {
        this.groupCRUD = groupCRUD;
    }

    @PostMapping
    public ResponseEntity<GroupResponse> addNewGroup(@RequestParam("name") String name,
                                                     @RequestParam("description") String description) {
        Group group = groupCRUD.addNewGroup(name, description);
        return ResponseEntity.ok(GroupResponse.createFromGroup(group));
    }

    @GetMapping
    public ResponseEntity<String> test() {
        return ResponseEntity.ok("Teste");
    }
}
