package br.edu.ifsp.scl.pipegene.usecases.account;

import br.edu.ifsp.scl.pipegene.usecases.account.gateway.UserApplicationDAO;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.account.model.CreateApplicationUser;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationUserService implements UserDetailsService, ApplicationUserCRUD {

    private final UserApplicationDAO userApplicationDAO;

    public ApplicationUserService(UserApplicationDAO userApplicationDAO) {
        this.userApplicationDAO = userApplicationDAO;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        return userApplicationDAO.findUserByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("Username %s not found", username)));
    }

    @Override
    public ApplicationUser registerNewUser(CreateApplicationUser user) {
        return userApplicationDAO.saveNewUser(user.toApplicationUser());
    }

    @Override
    public ApplicationUser findUserById(UUID userId) {
        return userApplicationDAO.findUserById(userId)
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with id %s not found", userId)));
    }

    @Override
    public Optional<ApplicationUser> updateUserById(UUID userId, CreateApplicationUser userRequest) {
        return userApplicationDAO.findUserById(userId)
                .map(user -> userRequest.toApplicationUser())
                .flatMap(userApplicationDAO::updateUser);
    }
}
