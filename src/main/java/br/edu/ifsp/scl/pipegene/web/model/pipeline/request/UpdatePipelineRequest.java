package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;

import java.util.ArrayList;

public class UpdatePipelineRequest {

    private String description;

    private ArrayList<PipelineStepRequest> steps;

    public UpdatePipelineRequest() { }

    public Pipeline convertToPipeline() {
        return Pipeline.getNewInstanceWithDescriptionAndSteps(description, steps == null ? new ArrayList<>() : steps.stream()
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
}
