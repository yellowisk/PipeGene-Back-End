package br.edu.ifsp.scl.pipegene.usecases.pipeline;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.CreatePipelineRequest;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.PipelineStepDTO;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.UpdatePipelineRequest;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.UpdatePipelineStepRequest;

import java.util.List;
import java.util.UUID;

public interface PipelineCRUD {

    Pipeline addNewPipeline(UUID projectId, CreatePipelineRequest request);

    List<Pipeline> findAllPipeline(UUID projectId);

    Pipeline findByProjectIdAndPipelineId(UUID projectId, UUID pipelineId);

    List<Pipeline> listAllPipelinesByUserId(UUID userId);

    PipelineStepDTO findPipelineStepById(UUID projectId, UUID pipelineId, UUID stepId);
    List<PipelineStepDTO> listAllPipelineStepsByPipelineId(UUID projectId, UUID pipelineId);



    //TODO: VERIFY PROJECT EDITING FUNCTIONALITIES
    Pipeline updatePipelineSteps(UUID projectId, UUID pipelineId, List<UpdatePipelineStepRequest> requests);
    Pipeline updatePipelineHeader(UUID projectId, UUID pipelineId, UpdatePipelineRequest pipelineRequest);

}
