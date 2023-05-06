package br.edu.ifsp.scl.pipegene.external.persistence;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.Collection;
import java.util.UUID;

@Repository
public class GroupDAOimpl implements GroupDAO {

    @Value("${queries.sql.group-dao.insert.group}")
    private String insertGroupQuery;

    private final JdbcTemplate jdbcTemplate;

    public GroupDAOimpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public Group saveGroup(Group group) {
        jdbcTemplate.update(insertGroupQuery, group.getId(), group.getName(), group.getDescription(), group.getOwnerId());

        jdbcTemplate.batchUpdate(insertGroupQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, group.getId());
                ps.setString(2, group.getName());
                ps.setString(3, group.getDescription());
                ps.setObject(4, group.getOwnerId());
            }

            @Override
            public int getBatchSize() {
                return 1;
            }
        });
        return group;
    }

    @Override
    public GroupParticipation saveGroupParticipation(GroupParticipation groupParticipation) {
        return null;
    }

    @Override
    public void acceptGroupParticipation(GroupParticipation groupParticipation) {

    }

    @Override
    public void rejectGroupParticipation(GroupParticipation groupParticipation) {

    }

    @Override
    public void leaveGroup(GroupParticipation groupParticipation) {

    }

    @Override
    public Collection<Group> findAllGroupsByUserId(UUID userId) {
        return null;
    }

    @Override
    public Collection<GroupParticipation> findAllGroupParticipationsByGroupId(UUID groupId) {
        return null;
    }
}
