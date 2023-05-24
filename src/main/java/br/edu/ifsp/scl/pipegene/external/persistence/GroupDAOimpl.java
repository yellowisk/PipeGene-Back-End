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

import java.awt.*;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.sql.Types;
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

    private final JdbcTemplate jdbcTemplate;

    public GroupDAOimpl(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Transactional
    @Override
    public Group saveGroup(Group group) {
        jdbcTemplate.update(insertGroupQuery, group.getId(), group.getName(), group.getDescription(), group.getOwnerId());
        return Group.createWithOnlyId(group.getId());
    }

    @Override
    public GroupParticipation saveGroupParticipation(GroupParticipation groupParticipation) {
        jdbcTemplate.update(saveGroupParticipationQuery, groupParticipation.getId(),
                groupParticipation.getGroup().getId(), groupParticipation.getReceiverId(),
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
            groupParticipation = jdbcTemplate.queryForObject(findGroupParticipationByIdQuery, (rs, rowNum) -> {
                UUID id = (UUID) rs.getObject("id");
                UUID groupId = (UUID) rs.getObject("group_id");
                UUID receiveUserId = (UUID) rs.getObject("receive_user_id");
                UUID submitterUserId = (UUID) rs.getObject("submitter_user_id");
                Timestamp createdDate = rs.getTimestamp("create_date");
                GroupParticipationStatusEnum status = GroupParticipationStatusEnum.valueOf(rs.getString("status"));
                return GroupParticipation.createWithAllFields(id, Group.createWithOnlyId(groupId), receiveUserId, status, submitterUserId, createdDate);
            }, groupParticipationId);
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
            Group group = jdbcTemplate.queryForObject(findGroupByIdQuery, (rs, rowNum) -> {
                UUID id = (UUID) rs.getObject("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                UUID ownerId = (UUID) rs.getObject("owner_id");
                return Group.createWithoutGroupParticipations(id, name, description, ownerId);
            }, groupId);
            if (Objects.isNull(group))
                throw new IllegalStateException();

            List<GroupParticipation> groupParticipationList = jdbcTemplate.query(findGroupParticipationByGroupIdQuery, (rs, rowNum) -> {
                UUID id = (UUID) rs.getObject("id");
                UUID receiveUserId = (UUID) rs.getObject("receive_user_id");
                UUID submitterUserId = (UUID) rs.getObject("submitter_user_id");
                Timestamp createdDate = rs.getTimestamp("create_date");
                GroupParticipationStatusEnum status = GroupParticipationStatusEnum.valueOf(rs.getString("status"));
                return GroupParticipation.createWithAllFields(id, group, receiveUserId, status, submitterUserId, createdDate);
            }, groupId);
            groupParticipationList.forEach(group::addParticipation);

            return Optional.of(group);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
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
            groupParticipation = jdbcTemplate.queryForObject(selectGroupParticipationByGroupIdAndReceiverIdQuery, (rs, rowNum) -> {
                UUID id = (UUID) rs.getObject("id");
                UUID groupIdNew = (UUID) rs.getObject("group_id");
                UUID receiveUserId = (UUID) rs.getObject("receive_user_id");
                UUID submitterUserId = (UUID) rs.getObject("submitter_user_id");
                Timestamp createdDate = rs.getTimestamp("create_date");
                GroupParticipationStatusEnum status = GroupParticipationStatusEnum.valueOf(rs.getString("status"));
                return GroupParticipation.createWithAllFields(id, Group.createWithOnlyId(groupIdNew), receiveUserId, status, submitterUserId, createdDate);
            }, groupId, receiverId);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }

        if (Objects.isNull(groupParticipation))
            throw new IllegalStateException();

        return Optional.of(groupParticipation);
    }

}

