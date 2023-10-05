package br.edu.ifsp.scl.pipegene.external.persistence;

import br.edu.ifsp.scl.pipegene.domain.Dataset;
import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.domain.Project;
import br.edu.ifsp.scl.pipegene.usecases.pipeline.gateway.PipelineDAO;
import br.edu.ifsp.scl.pipegene.usecases.project.gateway.ProjectDAO;
import br.edu.ifsp.scl.pipegene.web.exception.GenericResourceException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.BatchPreparedStatementSetter;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

@Repository
public class ProjectDAOImpl implements ProjectDAO {

    public final static UUID OWNER_ID = UUID.fromString("78cec5db-6396-4fd9-803f-1fd469d76330");

    private final JdbcTemplate jdbcTemplate;
    private final PipelineDAO pipelineDAO;
    private final DatasetDAO datasetDAO;

    @Value("${queries.sql.project-dao.insert.project}")
    private String insertProjectQuery;

    @Value("${queries.sql.project-dao.insert.dataset}")
    private String insertDatasetQuery;

    @Value("${queries.sql.project-dao.select.project-by-id}")
    private String selectProjectByIdQuery;

    @Value("${queries.sql.project-dao.select.project-all}")
    private String selectAllProjectQuery;

    @Value("${queries.sql.project-dao.select.project-all-by-user}")
    private String selectAllProjectByUserQuery;

    @Value("${queries.sql.project-dao.select.project-by-group-participation-id}")
    private String selectProjectByGroupParticipantIdQuery;

    @Value("${queries.sql.project-dao.select.project-by-pipeline-id}")
    private String selectProjectByPipelineIdQuery;

    @Value("${queries.sql.project-dao.exists.project-id}")
    private String existsProjectIdQuery;

    @Value("${queries.sql.project-dao.exists.execution-not-finished}")
    private String existsProjectIdExecutionNotFinishedQuery;

    @Value("${queries.sql.project-dao.update.project}")
    private String updateProjectQuery;

    @Value("${queries.sql.project-dao.delete.project-by-id}")
    private String deleteProjectByIdQuery;

    @Value("${queries.sql.project-dao.exists.is-owner}")
    private String isOwnerQuery;

    public ProjectDAOImpl(JdbcTemplate jdbcTemplate, PipelineDAO pipelineDAO, DatasetDAO datasetDAO) {
        this.jdbcTemplate = jdbcTemplate;
        this.pipelineDAO = pipelineDAO;
        this.datasetDAO = datasetDAO;
    }

    @Transactional
    @Override
    public Project saveNewProject(String name, String description, UUID groupId, List<Dataset> datasets, UUID ownerId) {
        UUID projectId = UUID.randomUUID();
        jdbcTemplate.update(insertProjectQuery, projectId, name, description, groupId, ownerId);

        jdbcTemplate.batchUpdate(insertDatasetQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, datasets.get(i).getId());
                ps.setString(2, datasets.get(i).getFilename());
                ps.setObject(3, projectId);
            }

            @Override
            public int getBatchSize() {
                return datasets.size();
            }
        });

        return Project.createWithoutPipelines(projectId, datasets, name, groupId, description, ownerId);
    }

    @Override
    public Project updateProject(Project project) {
        jdbcTemplate.update(updateProjectQuery, project.getName(), project.getDescription(), project.getId());
        return project;
    }

    @Override
    public Optional<Project> findProjectById(UUID id) {
        try {
            Project project = jdbcTemplate.queryForObject(selectProjectByIdQuery, (rs, rowNum) -> {
                String name = rs.getString("name");
                String description = rs.getString("description");
                UUID groupId = (UUID) rs.getObject("group_id");
                UUID ownerId = (UUID) rs.getObject("owner_id");

                return Project.createWithoutDatasetsAndPipelines(id, name, description, groupId, ownerId);
            }, id);

            if (Objects.isNull(project)) {
                throw new IllegalStateException();
            }

            datasetDAO.findDatasetsByProjectId(id).forEach(project::addDataset);


            List<Pipeline> pipelines = pipelineDAO.findPipelinesByProjectId(project.getId())
                    .stream().peek(pipeline -> pipeline.setProject(project))
                    .collect(Collectors.toList());
            project.addPipeline(pipelines);

            return Optional.of(project);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<Project> findAllProjectsByUser(UUID userId) {
        Map<UUID, Project> projects = jdbcTemplate.query(selectAllProjectByUserQuery, this::mapperProjectFromRs, userId).stream()
                .distinct().collect(Collectors.toMap(Project::getId, Function.identity()));

        return findPipelines(projects);
    }

    @Override
    public Optional<Project> findByPipelineId(UUID pipelineId) {
        Project project = jdbcTemplate.queryForObject(selectProjectByPipelineIdQuery, this::mapperProjectFromRs, pipelineId);

        datasetDAO.findDatasetsByProjectId(project.getId()).forEach(project::addDataset);


        List<Pipeline> pipelines = pipelineDAO.findPipelinesByProjectId(project.getId())
                .stream().peek(pipeline -> pipeline.setProject(project))
                .collect(Collectors.toList());
        project.addPipeline(pipelines);

        return Optional.of(project);
    }

    @Override
    public Optional<Project> findProjectByGroupParticipantId(UUID groupParticipantId) {
        try {
            Project project = jdbcTemplate.queryForObject(selectProjectByGroupParticipantIdQuery, (rs, rowNum) -> {
                UUID id = (UUID) rs.getObject("id");
                String name = rs.getString("name");
                String description = rs.getString("description");
                UUID groupId = (UUID) rs.getObject("group_id");
                UUID ownerId = (UUID) rs.getObject("owner_id");

                return Project.createWithoutDatasetsAndPipelines(id, name, description, groupId, ownerId);
            }, groupParticipantId);
            return Optional.of(project);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public Boolean projectExists(UUID projectId) {
        Boolean exists = jdbcTemplate.queryForObject(existsProjectIdQuery, Boolean.class, projectId);
        return Objects.nonNull(exists) && exists;
    }

    @Override
    public List<Project> findAllProjects() {
        Map<UUID, Project> projects = jdbcTemplate.query(selectAllProjectQuery, this::mapperProjectFromRs).stream()
                .collect(Collectors.toMap(Project::getId, Function.identity()));

        return findPipelines(projects);
    }

    private List<Project> findPipelines(Map<UUID, Project> projects) {
        Collection<UUID> projectIds = projects.keySet();

        datasetDAO.findDatasetsByProjectIds(projectIds)
                .forEach(dataset -> projects.get(dataset.getProjectId()).addDataset(dataset));

        projectIds.forEach(id -> {
            Project project = projects.get(id);
            List<Pipeline> pipelines = pipelineDAO.findPipelinesByProjectId(id).stream()
                    .peek(pipeline -> pipeline.setProject(project))
                    .collect(Collectors.toList());
            project.addPipeline(pipelines);
        });

        return new ArrayList<>(projects.values());
    }

    @Transactional
    @Override
    public boolean deleteProjectById(UUID id) {
        Boolean existsExecutionNotFinished = jdbcTemplate.queryForObject(existsProjectIdExecutionNotFinishedQuery, Boolean.class, id);
        if (Objects.nonNull(existsExecutionNotFinished) && existsExecutionNotFinished) {
            return false;
        }

        if (jdbcTemplate.update(deleteProjectByIdQuery, id) != 1) {
            throw new GenericResourceException("Unexpected error when try delete project with id=" + id, "Exclusion Error");
        }

        return true;
    }

    @Override
    public boolean isOwner(UUID projectId, UUID userId) {
        return jdbcTemplate.queryForObject(isOwnerQuery, Boolean.class, projectId, userId);
    }

    private Project mapperProjectFromRs(ResultSet rs, int rowNum) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        UUID groupId = (UUID) rs.getObject("group_id");
        UUID ownerId = (UUID) rs.getObject("owner_id");


        return Project.createWithoutDatasetsAndPipelines(id, name, description, groupId, ownerId);
    }
}
