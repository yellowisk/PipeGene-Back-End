package br.edu.ifsp.scl.pipegene.usecases.account;

import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.external.persistence.UserApplicationDAOImpl;
import br.edu.ifsp.scl.pipegene.usecases.account.gateway.UserApplicationDAO;
import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.account.model.CreateApplicationUser;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import br.edu.ifsp.scl.pipegene.web.model.account.request.UserRequest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.UUID;

@Service
public class ApplicationUserService implements UserDetailsService, ApplicationUserCRUD {

    private final UserApplicationDAO userApplicationDAO;
    private final JdbcTemplate jdbcTemplate;

    public ApplicationUserService(UserApplicationDAO userApplicationDAO) {
        this.userApplicationDAO = userApplicationDAO;
        this.jdbcTemplate = new JdbcTemplate();
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
                .orElseThrow(() -> new UsernameNotFoundException(String.format("User with id" + userId + "not found")));
    }

    @Override
    public ApplicationUser updateUser(UUID userId, UserRequest request) {
        Optional<ApplicationUser> optional = userApplicationDAO.findUserById(userId);

        if(optional.isEmpty()) {
            throw new ResourceNotFoundException("Not found user with id: " + userId);
        }

        ApplicationUser user = optional.get();

        return userApplicationDAO.updateUser(user.getNewInstanceWithId(userId));
    }
}
