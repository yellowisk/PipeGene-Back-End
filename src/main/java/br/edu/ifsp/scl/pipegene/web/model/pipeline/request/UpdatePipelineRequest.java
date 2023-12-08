package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.domain.PipelineStatus;

import java.util.ArrayList;

public class UpdatePipelineRequest {

    private String description;
    private String status;

    private ArrayList<PipelineStepRequest> steps;

    public UpdatePipelineRequest() { }

    public Pipeline convertToPipeline() {
        return Pipeline.getNewInstanceWithDescriptionAndStatusAndSteps(description, PipelineStatus.valueOf(status),
                steps == null ? new ArrayList<>() : steps.stream()
                .map(PipelineStepRequest::convertToPipelineStep)
                .toList());
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public ArrayList<PipelineStepRequest> getSteps() {
        return steps;
    }

    public void setSteps(ArrayList<PipelineStepRequest> steps) {
        this.steps = steps;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }
}
