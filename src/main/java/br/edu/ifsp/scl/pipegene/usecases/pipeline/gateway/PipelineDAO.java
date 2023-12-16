package br.edu.ifsp.scl.pipegene.usecases.pipeline.gateway;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.domain.PipelineStep;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.PipelineStepDTO;

import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface PipelineDAO {

    Pipeline savePipeline(Pipeline pipeline);

    List<Pipeline> findAll(UUID projectId);

    List<Pipeline> listAllByOwnerId(UUID ownerId);

    Collection<Pipeline> findPipelinesByProjectId(UUID projectId);

    Optional<Pipeline> findPipelineById(UUID pipelineId);

    Optional<Pipeline> findFullPipelineById(UUID projectId, UUID pipelineId);

    List<PipelineStepDTO> findAllPipelineStepsByPipelineId(UUID pipelineId);

    PipelineStepDTO findPipelineStepById(UUID pipelineId);

    Pipeline updatePipeline(Pipeline pipeline);

    Pipeline addPipelineStep(UUID pipelineId, PipelineStep pipelineStep);

    Pipeline clonePipeline(Pipeline pipeline);

    Pipeline deletePipeline(List<PipelineStep> steps, UUID stepId);

    Boolean pipelineExists(UUID pipelineId);

}
