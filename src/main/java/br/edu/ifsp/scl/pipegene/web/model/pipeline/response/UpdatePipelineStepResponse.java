package br.edu.ifsp.scl.pipegene.web.model.pipeline.response;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.domain.PipelineStep;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.PipelineStepDTO;

import java.util.List;

public class UpdatePipelineStepResponse {
    private String description;

    private List<PipelineStep> pipelineSteps;

    public UpdatePipelineStepResponse
            (List<PipelineStep> pipelineSteps)
    {
        this.pipelineSteps = pipelineSteps;
    }

    public UpdatePipelineStepResponse() {
    }

    public static UpdatePipelineStepResponse createFromPipelineStepDTOList(List<PipelineStepDTO> pipelineStepDTOList) {
        return new UpdatePipelineStepResponse(
                PipelineStepDTO.createListFromPipelineStepDTOList(pipelineStepDTOList)
        );
    }

    public List<PipelineStep> getPipelineSteps() {
        return pipelineSteps;
    }

    public void setPipelineSteps(List<PipelineStep> pipelineSteps) {
        this.pipelineSteps = pipelineSteps;
    }
}
