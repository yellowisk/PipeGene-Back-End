package br.edu.ifsp.scl.pipegene.external.persistence;

import br.edu.ifsp.scl.pipegene.domain.*;
import br.edu.ifsp.scl.pipegene.external.persistence.util.JsonUtil;
import br.edu.ifsp.scl.pipegene.usecases.pipeline.gateway.PipelineDAO;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.PipelineStepDTO;

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
public class PipelineDAOImpl implements PipelineDAO {

    private final JdbcTemplate jdbcTemplate;
    private final JsonUtil jsonUtil;

    @Value("${queries.sql.pipeline-dao.insert.pipeline}")
    private String insertPipelineQuery;

    @Value("${queries.sql.pipeline-dao.insert.pipeline-step}")
    private String insertPipelineStepQuery;

    @Value("${queries.sql.pipeline-dao.select.pipeline-all}")
    private String selectAllPipelineQuery;

    @Value("${queries.sql.pipeline-dao.select.pipelines-by-project-id}")
    private String selectPipelinesByProjectIdQuery;

    @Value("${queries.sql.pipeline-dao.select.pipelines-by-owner-id}")
    private String selectPipelinesByOwnerIdQuery;

    @Value("${queries.sql.pipeline-dao.select.pipeline-by-id}")
    private String selectPipelineByIdQuery;

    @Value("${queries.sql.pipeline-dao.select.pipeline-with-project-id-by-id}")
    private String selectPipelineWithProjectIdByIdQuery;

    @Value("${queries.sql.pipeline-dao.select.pipeline-by-provider-id}")
    private String selectPipelineByProviderIdQuery;

    @Value("${queries.sql.pipeline-dao.select.all-pipeline-by-provider-and-project-id}")
    private String selectPipelineByProviderAndProjectIdQuery;

    @Value("${queries.sql.pipeline-dao.select.pipeline-steps-by-pipeline-ids}")
    private String selectPipelineStepsByPipelineIdsQuery;

    @Value("${queries.sql.pipeline-dao.select.pipeline-steps-by-pipeline-id}")
    private String selectPipelineStepsByPipelineIdQuery;

    @Value("${queries.sql.pipeline-dao.select.pipeline-steps-all-data-and-service-data-by-pipeline-id}")
    private String selectPipelineStepsConnectionToProviderByPipelineIdQuery;

    @Value("${queries.sql.pipeline-dao.select.pipeline-all-data-w/-provider-id}")
    private String selectPipelineStepsConnectionQuery;

    @Value("${queries.sql.pipeline-dao.update.pipeline-by-id}")
    private String updatePipelineByIdQuery;

    @Value("${queries.sql.pipeline-dao.update.pipeline-step-by-id}")
    private String updatePipelineStepByIdQuery;

    @Value("${queries.sql.pipeline-dao.update.pipeline-status-by-id}")
    private String updatePipelineStatusByIdQuery;

    @Value("${queries.sql.pipeline-dao.update.step}")
    private String updateStepQuery;

    @Value("${queries.sql.pipeline-dao.delete.pipeline-step-by-id}")
    private String deletePipelineStepsQuery;


    public PipelineDAOImpl(JdbcTemplate jdbcTemplate, JsonUtil jsonUtil) {
        this.jdbcTemplate = jdbcTemplate;
        this.jsonUtil = jsonUtil;
    }

    @Override
    public Boolean pipelineExists(UUID pipelineId) {
        try {
            jdbcTemplate.queryForObject(selectPipelineByIdQuery, (rs, rowNum) -> null, pipelineId);
            return true;
        } catch (EmptyResultDataAccessException e) {
            return false;
        }
    }

    @Transactional
    @Override
    public Pipeline savePipeline(Pipeline pipeline) {
        UUID pipelineId = UUID.randomUUID();
        jdbcTemplate.update(insertPipelineQuery, pipelineId, pipeline.getProjectId(), pipeline.getDescription(), pipeline.getStatus().name());

        List<PipelineStep> steps = pipeline.getSteps();

        jdbcTemplate.batchUpdate(insertPipelineStepQuery, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, steps.get(i).getStepId());
                ps.setObject(2, pipelineId);
                ps.setObject(3, steps.get(i).getProvider().getId());
                ps.setString(4, steps.get(i).getInputType());
                ps.setString(5, steps.get(i).getOutputType());
                ps.setString(6, jsonUtil.writeMapStringObjectAsJsonString(steps.get(i).getParams()));
                ps.setInt(7, steps.get(i).getStepNumber());
            }

            @Override
            public int getBatchSize() {
                return steps.size();
            }
        });

        return pipeline.getNewInstanceWithId(pipelineId);
    }

    @Transactional
    @Override
    public Pipeline clonePipeline(Pipeline pipeline) {
        UUID pipelineId = UUID.randomUUID();
        jdbcTemplate.update(insertPipelineQuery, pipelineId, pipeline.getProjectId(), pipeline.getDescription(), pipeline.getStatus().name());

        List<PipelineStep> steps = pipeline.getSteps();

        jdbcTemplate.batchUpdate(insertPipelineStepQuery, new BatchPreparedStatementSetter() {

            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setObject(1, UUID.randomUUID());
                ps.setObject(2, pipelineId);
                ps.setObject(3, steps.get(i).getProvider().getId());
                ps.setString(4, steps.get(i).getInputType());
                ps.setString(5, steps.get(i).getOutputType());
                ps.setString(6, jsonUtil.writeMapStringObjectAsJsonString(steps.get(i).getParams()));
                ps.setInt(7, steps.get(i).getStepNumber());
            }

            @Override
            public int getBatchSize() {
                return steps.size();
            }
        });

        return pipeline.getNewInstanceWithId(pipelineId);
    }

    @Override
    public List<Pipeline> listAllByOwnerId(UUID ownerId) {
        Collection<Pipeline> pipelines = retrieveAllBasedQueryWithAnIdCondition(selectPipelinesByOwnerIdQuery, ownerId);
        return new ArrayList<>(pipelines);
    }

    @Override
    public List<Pipeline> listAllPipelineByProjectAndProvider(UUID projectId, UUID providerId) {
        List<Pipeline> pipelinesFromProject = findPipelinesByProjectId(projectId).stream().toList();
        List<Pipeline> pipelinesByProviderId = pipelinesFromProject.stream()
                .filter(pipeline -> pipeline.getSteps().stream().anyMatch(step -> step.getProvider().getId().equals(providerId))).toList();
        return pipelinesByProviderId;
    }

    private Collection<Pipeline> retrievePipelinesByQueryAndParams(String query, Object... params) {
        Map<UUID, Pipeline> pipelineMap = new HashMap<>();

        jdbcTemplate.query(query, (rs, rowNum) -> {
            try {
                UUID pipelineId = (UUID) rs.getObject("pipeline_id");
                PipelineStep step = PipelineStep.of(
                        (UUID) rs.getObject("pipeline_step_id"),
                        Provider.createWithOnlyId((UUID) rs.getObject("pipeline_step_provider_id")),
                        rs.getString("pipeline_step_input_type"),
                        rs.getString("pipeline_step_output_type"),
                        jsonUtil.retrieveStepParams(rs.getString("pipeline_step_params")),
                        rs.getInt("pipeline_step_number"),
                        Pipeline.createWithOnlyId(pipelineId)
                );

                if (pipelineMap.containsKey(pipelineId)) {
                    pipelineMap.get(pipelineId).addStep(step);
                } else {
                    String description = rs.getString("pipeline_description");
                    PipelineStatus status = PipelineStatus.valueOf(rs.getString("pipeline_status"));

                    Pipeline pipeline = new Pipeline(pipelineId, null, description, status, step);
                    pipelineMap.put(pipelineId, pipeline);
                }
            } catch (Exception e) {
                e.printStackTrace();
                throw new SQLException();
            }

            return null;
        }, params);

        Collection<Pipeline> pipelines = pipelineMap.values();
        pipelines.forEach(Pipeline::sortedSteps);

        return pipelines;
    }

    @Override
    public List<Pipeline> findAll(UUID projectId) {
        return new ArrayList<>(retrievePipelinesByQueryAndParams(selectAllPipelineQuery, projectId));
    }

    @Override
    public List<Pipeline> listAllPipelinesByProvider(UUID providerId) {
        return new ArrayList<>(retrievePipelinesByQueryAndParams(selectPipelineByProviderIdQuery, providerId));
    }

    @Override
    public Collection<Pipeline> findPipelinesByProjectId(UUID projectId) {
        return retrieveAllBasedQueryWithAnIdCondition(selectPipelinesByProjectIdQuery, projectId);
    }

    @Override
    public List<Pipeline> listAllPipelinesByProviderAndProjectId(UUID projectId, UUID providerId){
        return new ArrayList<>(retrievePipelinesByQueryAndParams(selectPipelineByProviderAndProjectIdQuery, providerId, projectId));
    }

    private Collection<Pipeline> retrieveAllBasedQueryWithAnIdCondition(String query, UUID id) {
        Map<UUID, Pipeline> pipelineMap = jdbcTemplate.query(query,
                (rs, rowNum) -> Pipeline.createWithoutProjectAndSteps((UUID) rs.getObject("id"),
                        rs.getString("description"), PipelineStatus.valueOf(rs.getString("status")))
                , id).stream().collect(Collectors.toMap(Pipeline::getId, Function.identity()));

        Object[] ids = pipelineMap.keySet().toArray();

        List<PipelineStep> steps = jdbcTemplate.query(selectPipelineStepsByPipelineIdsQuery,
                ps -> ps.setObject(1, ps.getConnection().createArrayOf("uuid", ids)),
                this::mapperPipelineStepFromRs);
        steps.forEach(step -> pipelineMap.get(step.getPipelineId()).addStep(step));

        return pipelineMap.values().stream()
                .peek(Pipeline::sortedSteps)
                .collect(Collectors.toList());
    }

    @Transactional
    @Override
    public Optional<Pipeline> findPipelineById(UUID pipelineId) {
        try {
            Pipeline pipeline = jdbcTemplate.queryForObject(selectPipelineByIdQuery, (rs, rowNum) -> {
                UUID id = (UUID) rs.getObject("id");
                String description = rs.getString("description");
                PipelineStatus status = PipelineStatus.valueOf(rs.getString("status"));

                return Pipeline.createWithoutProjectAndSteps(id, description, status);
            }, pipelineId);

            if (Objects.isNull(pipeline)) {
                throw new IllegalStateException();
            }

            List<PipelineStep> steps = jdbcTemplate.query(selectPipelineStepsByPipelineIdQuery,
                    this::mapperPipelineStepFromRs, pipelineId);
            steps.forEach(pipeline::addStep);

            return Optional.of(pipeline);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Transactional
    @Override
    public Optional<Pipeline> findFullPipelineById(UUID projectId, UUID pipelineId) {
        try {
            Pipeline pipeline = jdbcTemplate.queryForObject(selectPipelineWithProjectIdByIdQuery, (rs, rowNum) -> {
                UUID id = (UUID) rs.getObject("id");
                UUID queryProjectId = (UUID) rs.getObject("project_id");
                String description = rs.getString("description");
                PipelineStatus status = PipelineStatus.valueOf(rs.getString("status"));

                return Pipeline.createWithoutSteps(id, Project.createWithId(queryProjectId), description, status);
            }, pipelineId);

            if (Objects.isNull(pipeline)) {
                throw new IllegalStateException(
                        "Couldn't find pipeline with id: " + pipelineId + ".");
            }

            if (!projectId.equals(pipeline.getProjectId())) {
                throw new IllegalStateException(
                        "Couldn't find pipeline: " + pipelineId + " in the project: " + projectId +
                                ". Actual pipeline's project is " + pipeline.getProjectId() + ".");
            }

            List<PipelineStep> steps = jdbcTemplate.query(selectPipelineStepsByPipelineIdQuery,
                    this::mapperPipelineStepFromRs, pipelineId);
            steps.forEach(pipeline::addStep);

            return Optional.of(pipeline);
        } catch (EmptyResultDataAccessException e) {
            return Optional.empty();
        }
    }

    @Override
    public List<PipelineStepDTO> findAllPipelineStepsByPipelineId(UUID pipelineId) {
        Optional<Pipeline> pipelineOptional = findPipelineById(pipelineId);

        if (pipelineOptional.isEmpty()) {
            throw new IllegalStateException("Couldn't find pipeline with id: " + pipelineId);
        }

        Pipeline pipeline = pipelineOptional.get();

        Map<UUID, PipelineStepDTO> pipelineStepMap = jdbcTemplate.query(
                        selectPipelineStepsConnectionQuery,
                        ps -> ps.setObject(1, pipelineId),
                        this::shortMapperPipelineStepFromRs)
                .stream()
                .collect(Collectors.toMap(PipelineStepDTO::getStepId, Function.identity()));

        pipelineStepMap.values().forEach(pipeline::addStepDTO);

        return new ArrayList<>(pipelineStepMap.values());
    }

    @Override
    public PipelineStepDTO findPipelineStepById(UUID pipelineId) {
        List<PipelineStepDTO> pipelineSteps = jdbcTemplate.query(
                selectPipelineStepsByPipelineIdQuery,
                ps -> {
                    ps.setObject(1, pipelineId);
                },
                this::shortMapperPipelineStepFromRs
        );

        if (pipelineSteps.isEmpty()) {
            throw new IllegalStateException("Couldn't find pipeline step with id: " + pipelineId);
        }

        return pipelineSteps.get(0);
    }

    @Transactional
    @Override
    public Pipeline updatePipeline(Pipeline pipeline) {
        UUID pipelineId = pipeline.getId();

        jdbcTemplate.update(updatePipelineByIdQuery, pipeline.getDescription(), pipeline.getStatus().name(), pipelineId);

        List<PipelineStep> steps = pipeline.getSteps();
        jdbcTemplate.batchUpdate(updateStepQuery, new BatchPreparedStatementSetter() {
            @Override
            public void setValues(PreparedStatement ps, int i) throws SQLException {
                ps.setString(1, steps.get(i).getInputType());
                ps.setString(2, steps.get(i).getOutputType());
                ps.setObject(3, steps.get(i).getProvider().getId());
                ps.setString(4, jsonUtil.writeMapStringObjectAsJsonString(steps.get(i).getParams()));
                ps.setInt(5, steps.get(i).getStepNumber());
                ps.setObject(6, steps.get(i).getStepId());
            }
            @Override
            public int getBatchSize() {
                return steps.size();
            }
        });

        return pipeline;
    }

    @Override
    public Pipeline addPipelineStep(UUID pipelineId, PipelineStep pipelineStep) {
        jdbcTemplate.update(insertPipelineStepQuery,
                UUID.randomUUID(),
                pipelineId,
                pipelineStep.getProvider().getId(),
                pipelineStep.getInputType(),
                pipelineStep.getOutputType(),
                jsonUtil.writeMapStringObjectAsJsonString(pipelineStep.getParams()),
                pipelineStep.getStepNumber());

        return findPipelineById(pipelineId).orElseThrow(() -> new IllegalStateException("Couldn't find pipeline with id: " + pipelineId));
    }

    @Override
    public Pipeline deletePipelineStep(List<PipelineStep> steps, UUID stepId) {
        jdbcTemplate.update(deletePipelineStepsQuery, stepId);
        Pipeline pipeline = Pipeline.getNewInstanceWithDescriptionAndStatusAndSteps(null, null, steps);
        return pipeline;
    }

    @Override
    public Pipeline disablePipeline(Pipeline pipeline) {
        jdbcTemplate.update(updatePipelineStatusByIdQuery, pipeline.getId());
        return pipeline;
    }

    private PipelineStepDTO shortMapperPipelineStepFromRs(ResultSet rs, int rowNum) throws SQLException {
        UUID stepId = (UUID) rs.getObject("step_id");
        UUID providerId = (UUID) rs.getObject("provider_id");
        String inputType = rs.getString("input_type");
        String outputType = rs.getString("output_type");

        String params = rs.getString("params");
        Integer stepNumber = rs.getInt("step_number");

        String providerName = rs.getString("provider_name");
        String providerDescription = rs.getString("provider_description");

        Provider provider = Provider.createWithPartialValues(providerId, providerName, providerDescription);

        return new PipelineStepDTO(stepId, provider, inputType, outputType, jsonUtil.retrieveStepParams(params), stepNumber);
    }

    private PipelineStep mapperPipelineStepFromRs(ResultSet rs, int rowNum) throws SQLException {
        UUID stepId = (UUID) rs.getObject("step_id");
        UUID providerId = (UUID) rs.getObject("provider_id");
        String inputType = rs.getString("input_type");
        String outputType = rs.getString("output_type");

        String params = rs.getString("params");
        Integer stepNumber = rs.getInt("step_number");

        String providerName = rs.getString("provider_name");
        String providerDescription = rs.getString("provider_description");
        List<String> inputSupportedTypes =  Arrays.stream(
                rs.getString("provider_input_supported_types").split(",")
        ).map(String::trim).collect(Collectors.toList());

        List<String> outputSupportedTypes =  Arrays.stream(
                rs.getString("provider_output_supported_types").split(",")
        ).map(String::trim).collect(Collectors.toList());

        Provider provider = Provider.createWithPartialValues(providerId, providerName, providerDescription,
                inputSupportedTypes, outputSupportedTypes);

        return PipelineStep.of(stepId, provider, inputType, outputType, jsonUtil.retrieveStepParams(params), stepNumber,
                Pipeline.createWithOnlyId((UUID) rs.getObject("pipeline_id")));
    }
}
