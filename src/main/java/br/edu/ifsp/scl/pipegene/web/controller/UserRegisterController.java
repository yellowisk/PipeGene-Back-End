package br.edu.ifsp.scl.pipegene.web.controller;

import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.usecases.account.ApplicationUserCRUD;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.web.model.account.request.UserRequest;
import br.edu.ifsp.scl.pipegene.web.model.account.response.ApplicationUserResponse;
import br.edu.ifsp.scl.pipegene.web.model.account.request.CreateUserRequest;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.List;
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

    @GetMapping("/UsersByUsernameOrName/{usernameOrName}")
    public ResponseEntity<List<ApplicationUserResponse>> findUserByNameOrEmail(@PathVariable String usernameOrName) {
        List<ApplicationUser> applicationUser = applicationUserCRUD.findUsersByUsernameOrName(usernameOrName);

        return ResponseEntity.ok(
                applicationUser.stream()
                        .map(ApplicationUserResponse::createFromApplicationUser)
                        .toList()
        );
    }

    @PatchMapping("/{userId}")
    public ResponseEntity<ApplicationUserResponse> updateUser(@PathVariable UUID userId,
                                                   @RequestBody UserRequest userRequest) {
        ApplicationUser user = applicationUserCRUD.updateUser(userId, userRequest);

        return ResponseEntity.ok(ApplicationUserResponse.createFromApplicationUser(user));
    }
}
