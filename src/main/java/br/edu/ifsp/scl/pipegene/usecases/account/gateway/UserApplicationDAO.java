package br.edu.ifsp.scl.pipegene.usecases.account.gateway;

import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface UserApplicationDAO {

    Optional<ApplicationUser> findUserByUsername(String username);

    ApplicationUser saveNewUser(ApplicationUser user);

    Optional<ApplicationUser> findUserById(UUID userId);

    ApplicationUser updateUser(ApplicationUser user);

    List<ApplicationUser> findUsersByUsernameOrName(String UsernameOrName);

    List<ApplicationUser> findAllUsersByGroupId(UUID groupId);

}
