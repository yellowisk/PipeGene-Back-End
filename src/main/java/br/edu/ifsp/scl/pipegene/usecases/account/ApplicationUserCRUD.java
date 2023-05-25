package br.edu.ifsp.scl.pipegene.usecases.account;

import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.account.model.CreateApplicationUser;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationUserCRUD {

    ApplicationUser registerNewUser(CreateApplicationUser user);

    ApplicationUser findUserById(UUID userId);

    Optional<ApplicationUser> updateUserById(UUID userId, CreateApplicationUser userRequest);

}
