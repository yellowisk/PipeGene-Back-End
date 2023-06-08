package br.edu.ifsp.scl.pipegene.external.persistence;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipation;
import br.edu.ifsp.scl.pipegene.domain.GroupParticipationStatusEnum;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import br.edu.ifsp.scl.pipegene.web.exception.GenericResourceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import javax.xml.transform.Result;
import java.awt.*;
import java.sql.*;
import java.util.*;
import java.util.List;

@Repository
public class GroupDAOimpl implements GroupDAO {

    @Value("${queries.sql.group-dao.insert.group}")
    private String insertGroupQuery;

    @Value("${queries.sql.group-dao.select.group-by-id}")
    private String findGroupByIdQuery;

    @Value("${queries.sql.group-participation-dao.select.group-participation-by-group-id}")
    private String findGroupParticipationByGroupIdQuery;

    @Value("${queries.sql.group-participation-dao.insert.group-participation}")
    private String saveGroupParticipationQuery;

    @Value("${queries.sql.group-participation-dao.update.group-participation-status}")
    private String updateGroupParticipationStatusQuery;

    @Value("${queries.sql.group-participation-dao.select.group-participation-by-id}")
    private String findGroupParticipationByIdQuery;

    @Value("${queries.sql.group-participation-dao.delete.group-participation-by-id}")
    private String deleteGroupParticipationByIdQuery;

    @Value("${queries.sql.group-participation-dao.select.group-participation-by-group-id-and-receiver-id}")
    private String selectGroupParticipationByGroupIdAndReceiverIdQuery;

    @Value("${queries.sql.group-dao.select.group-all-owner-or-member}")
    private String selectAllGroupByUserId;

    private final JdbcTemplate jdbcTemplate;

    public GroupDAOimpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public Group saveGroup(Group group) {
        jdbcTemplate.update(insertGroupQuery, group.getId(), group.getOwnerId());
        return Group.createWithOnlyId(group.getId());
    }

    @Override
    public GroupParticipation saveGroupParticipation(GroupParticipation groupParticipation) {
        jdbcTemplate.update(saveGroupParticipationQuery, groupParticipation.getId(),
                groupParticipation.getGroupId(), groupParticipation.getReceiverId(),
                groupParticipation.getSubmitterId(), groupParticipation.getCreatedDate() ,groupParticipation.getStatus().name());

        return groupParticipation;
    }

    @Override
    public void updateGroupParticipation(GroupParticipation groupParticipation) {
        jdbcTemplate.update(updateGroupParticipationStatusQuery, ps -> {
            ps.setString(1, groupParticipation.getStatus().name());
            ps.setObject(2, groupParticipation.getId());
        });

    }

    @Override
    public Optional<GroupParticipation> findGroupParticipationById(UUID groupParticipationId) {
        GroupParticipation groupParticipation;
        try {
            groupParticipation = jdbcTemplate.queryForObject(findGroupParticipationByIdQuery,
                    this::mapperGroupParticipainFromRs, groupParticipationId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

        if (Objects.isNull(groupParticipation))
            throw new IllegalStateException();

        return Optional.of(groupParticipation);
    }

    @Override
    public Optional<Group> findGroupById(UUID groupId) {
        try {
            Group group = jdbcTemplate.queryForObject(findGroupByIdQuery, this::mapperGroupFromRs, groupId);
            if (Objects.isNull(group))
                throw new IllegalStateException();

            List<GroupParticipation> groupParticipationList = jdbcTemplate.query(findGroupParticipationByGroupIdQuery,
                    this::mapperGroupParticipainFromRs, groupId);
            group.addParticipationList(groupParticipationList);

            return Optional.of(group);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Group> findAllGroupByUserId(UUID userId) {
        return jdbcTemplate.query(selectAllGroupByUserId,
                this::mapperGroupFromRs, userId);
    }

    @Override
    @Transactional
    public GroupParticipation deleteGroupParticipation(UUID id) {
        if (jdbcTemplate.update(deleteGroupParticipationByIdQuery, id) != 1) {
            throw new GenericResourceException("Unexpected error when try delete project with id=" + id, "Exclusion Error");
        }
        return GroupParticipation.createOnlyWithId(id);
    }

    @Override
    public Optional<GroupParticipation> findGroupParticipationByGroupIdAndReceiverId(UUID groupId, UUID receiverId) {
        GroupParticipation groupParticipation;
        try {
            groupParticipation = jdbcTemplate.queryForObject(selectGroupParticipationByGroupIdAndReceiverIdQuery,
                    this::mapperGroupParticipainFromRs, groupId, receiverId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

        if (Objects.isNull(groupParticipation))
            throw new IllegalStateException();

        return Optional.of(groupParticipation);
    }

    private GroupParticipation mapperGroupParticipainFromRs(ResultSet rs, int rowNum) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID groupIdNew = (UUID) rs.getObject("group_id");
        UUID receiveUserId = (UUID) rs.getObject("receive_user_id");
        UUID submitterUserId = (UUID) rs.getObject("submitter_user_id");
        Timestamp createdDate = rs.getTimestamp("create_date");
        GroupParticipationStatusEnum status = GroupParticipationStatusEnum.valueOf(rs.getString("status"));
        return GroupParticipation.createWithAllFields(id, groupIdNew, receiveUserId, status, submitterUserId, createdDate);
    }

    private Group mapperGroupFromRs(ResultSet rs, int rowNum) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        UUID ownerId = (UUID) rs.getObject("owner_id");
        return Group.createWithoutGroupParticipations(id, ownerId);
    }

}

