package br.edu.ifsp.scl.pipegene.usecases.pipeline;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.*;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.CreateStepRequest;

import java.util.List;
import java.util.UUID;

public interface PipelineCRUD {

    Pipeline addNewPipeline(UUID projectId, CreatePipelineRequest request);

    List<Pipeline> findAllPipeline(UUID projectId);

    Pipeline findByProjectIdAndPipelineId(UUID projectId, UUID pipelineId);

    List<Pipeline> listAllPipelinesByUserId(UUID userId);
    List<Pipeline> listAllPipelineByProviderId(UUID projectId, UUID providerId);
    PipelineStepDTO findPipelineStepById(UUID projectId, UUID pipelineId, UUID stepId);
    List<PipelineStepDTO> listAllPipelineStepsByPipelineId(UUID projectId, UUID pipelineId);
    Pipeline updatePipeline(UUID projectId, UUID pipelineId, UpdatePipelineRequest request);
    Pipeline disablePipeline(UUID projectId, UUID pipelineId);
    Pipeline clonePipeline(UUID projectId, UUID pipelineId, ClonePipelineRequest request);
    Pipeline addNewPipelineStep(UUID pipelineId, CreateStepRequest request);
    Pipeline deletePipelineStep(UUID projectId, UUID pipelineId, UUID pipelineStepId);

}
