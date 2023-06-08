package br.edu.ifsp.scl.pipegene.usecases.pipeline.gateway;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.domain.PipelineStep;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.PipelineStepDTO;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.UpdatePipelineStepRequest;

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



    List<PipelineStepDTO> findAllPipelineStepsByPipelineId(UUID pipelineId);

    PipelineStepDTO findPipelineStepById(UUID pipelineId);

    Pipeline updatePipeline(Pipeline pipeline);

    Boolean pipelineExists(UUID pipelineId);

}
