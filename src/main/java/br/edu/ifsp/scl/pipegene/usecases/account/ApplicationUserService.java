package br.edu.ifsp.scl.pipegene.usecases.account;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
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

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
public class ApplicationUserService implements UserDetailsService, ApplicationUserCRUD {

    private final UserApplicationDAO userApplicationDAO;
    private final JdbcTemplate jdbcTemplate;

    private final IAuthenticationFacade authentication;

    public ApplicationUserService(UserApplicationDAO userApplicationDAO, JdbcTemplate jdbcTemplate, IAuthenticationFacade authentication) {
        this.userApplicationDAO = userApplicationDAO;
        this.jdbcTemplate = jdbcTemplate;
        this.authentication = authentication;
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

        ApplicationUser userRequest = request.toApplicationUser();

        return userApplicationDAO.updateUser(userRequest.getNewInstanceWithId(userId));
    }

    @Override
    public List<ApplicationUser> findUsersByUsernameOrName(String UsernameOrName) {
        return userApplicationDAO.findUsersByUsernameOrName(UsernameOrName)
                .stream().filter(user -> !user.getId()
                        .equals(authentication.getUserAuthenticatedId()))
                .collect(Collectors.toList());
    }

    @Override
    public List<ApplicationUser> getUsersByGroupId(UUID groupId) {
        return userApplicationDAO.findAllUsersByGroupId(groupId)
                .stream().filter(user -> !user.getId()
                        .equals(authentication.getUserAuthenticatedId()))
                .collect(Collectors.toList());
    }
}
