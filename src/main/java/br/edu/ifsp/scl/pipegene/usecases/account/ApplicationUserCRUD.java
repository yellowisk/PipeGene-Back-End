package br.edu.ifsp.scl.pipegene.usecases.account;

import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.account.model.CreateApplicationUser;
import br.edu.ifsp.scl.pipegene.web.model.account.request.UserRequest;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ApplicationUserCRUD {

    ApplicationUser registerNewUser(CreateApplicationUser user);

    ApplicationUser findUserById(UUID userId);

    ApplicationUser updateUser(UUID userId, UserRequest request);

    List<ApplicationUser> findUsersByUsernameOrName(String UsernameOrName);

}
