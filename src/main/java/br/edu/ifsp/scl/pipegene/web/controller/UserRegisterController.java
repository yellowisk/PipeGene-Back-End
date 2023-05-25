package br.edu.ifsp.scl.pipegene.web.controller;

import br.edu.ifsp.scl.pipegene.usecases.account.ApplicationUserCRUD;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.web.model.account.ApplicationUserResponse;
import br.edu.ifsp.scl.pipegene.web.model.account.CreateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.UUID;

@RestController
public class UserRegisterController {

    private final ApplicationUserCRUD applicationUserCRUD;

    public UserRegisterController(ApplicationUserCRUD applicationUserCRUD) {
        this.applicationUserCRUD = applicationUserCRUD;
    }

    @PostMapping("/register")
    public ResponseEntity<?> createUser(@RequestBody @Valid CreateUserRequest request) {
        ApplicationUser applicationUser = applicationUserCRUD.registerNewUser(request.toCreateUserApplication());

        return ResponseEntity.ok(
                ApplicationUserResponse.createFromApplicationUser(applicationUser)
        );
    }

    @GetMapping("/{userId}")
    public ResponseEntity<ApplicationUser> findUserById(@PathVariable UUID userId) {
        ApplicationUser applicationUser = applicationUserCRUD.findUserById(userId);

        return ResponseEntity.ok(applicationUser);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<ApplicationUser> updateUserById(@PathVariable UUID userId, @RequestBody @Valid CreateUserRequest request) {
        ApplicationUser applicationUser = applicationUserCRUD.updateUserById(userId, request.toCreateUserApplication()).get();

        return ResponseEntity.ok(applicationUser);
    }
}
