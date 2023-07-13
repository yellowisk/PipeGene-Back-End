package br.edu.ifsp.scl.pipegene.external.persistence;

import br.edu.ifsp.scl.pipegene.usecases.account.model.ApplicationUser;
import br.edu.ifsp.scl.pipegene.usecases.account.gateway.UserApplicationDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;

@Repository
public class UserApplicationDAOImpl implements UserApplicationDAO {

    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;

    @Value("${queries.sql.application-user-dao.insert.application-user}")
    private String insertApplicationUserQuery;

    @Value("${queries.sql.application-user-dao.select.application-user-by-username}")
    private String selectApplicationUserByUsernameQuery;

    @Value("${queries.sql.application-user-dao.select.application-user-by-id}")
    private String selectApplicationUserByIdQuery;

    @Value("${queries.sql.application-user-dao.update.application-user}")
    private String updateApplicationUserQuery;

    public UserApplicationDAOImpl(PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate) {
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Optional<ApplicationUser> findUserByUsername(String username) {
        try {
            ApplicationUser user = jdbcTemplate.queryForObject(selectApplicationUserByUsernameQuery,
                    this::mapperApplicationUserFromRs, username);

            if (Objects.isNull(user)) {
                throw new IllegalStateException();
            }

            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

    }

    @Override
    public Optional<ApplicationUser> findUserById(UUID userId) {
        try {
            ApplicationUser user = jdbcTemplate.queryForObject(selectApplicationUserByIdQuery,
                    this::mapperApplicationUserFromRs, userId);

            if (Objects.isNull(user)) {
                throw new IllegalStateException("Couldn't find user with id: " + userId);
            }

            return Optional.of(user);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public ApplicationUser updateUser(ApplicationUser user) {

        Optional<ApplicationUser> optionalUser = findUserById(user.getId());

        if (optionalUser.isEmpty()) {
            throw new IllegalStateException("Couldn't find user with id: " + user.getId());
        }

        jdbcTemplate.update(updateApplicationUserQuery, rs -> {
            rs.setString(1, user.getName());
            rs.setString(2, user.getUsername());
            rs.setString(3, user.getOrcid());
            rs.setString(4, user.getGithub());
            rs.setObject(5, user.getId());
        });

        return user;
    }

    @Transactional
    @Override
    public ApplicationUser saveNewUser(ApplicationUser user) {
        UUID id = UUID.randomUUID();

        jdbcTemplate.update(insertApplicationUserQuery, rs-> {
            rs.setObject(1, id);
            rs.setString(2, user.getName());
            rs.setString(3, user.getUsername());
            rs.setString(4, passwordEncoder.encode(user.getPassword()));
            rs.setString(5, user.getOrcid());
            rs.setString(6, user.getGithub());
            rs.setBoolean(7, user.isAccountNonExpired());
            rs.setBoolean(8, user.isAccountNonLocked());
            rs.setBoolean(9, user.isCredentialsNonExpired());
            rs.setBoolean(10, user.isEnabled());
        });

        return user.getNewInstanceWithId(id);
    }
    private ApplicationUser mapperApplicationUserFromRs(ResultSet rs, int rowNum) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        String username = rs.getString("username");
        String orcid = rs.getString("orcid");
        String github = rs.getString("github");
        String password = rs.getString("password");
        boolean isAccountNonExpired = rs.getBoolean("is_account_non_expired");
        boolean isAccountNonLocked = rs.getBoolean("is_account_nonLocked");
        boolean isCredentialsNonExpired = rs.getBoolean("is_credentials_non_expired");
        boolean isEnabled = rs.getBoolean("is_enabled");

        return new ApplicationUser(id, name, username, orcid, github, password, isAccountNonExpired,
                isAccountNonLocked, isCredentialsNonExpired, isEnabled);
    }
}
