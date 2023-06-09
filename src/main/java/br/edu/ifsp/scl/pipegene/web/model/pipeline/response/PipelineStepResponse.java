package br.edu.ifsp.scl.pipegene.web.model.pipeline.response;

import br.edu.ifsp.scl.pipegene.domain.PipelineStep;

import java.util.Map;
import java.util.UUID;

public class PipelineStepResponse {
    private UUID stepId;
    private UUID providerId;
    private String inputType;
    private String outputType;
    private Map<String, Object> params;
    private Integer stepNumber;

    public static PipelineStepResponse createFromPipelineStep(PipelineStep step) {
        return new PipelineStepResponse(
                step.getStepId(),
                step.getProvider().getId(),
                step.getInputType(),
                step.getOutputType(),
                step.getParams()
        );
    }

    public static PipelineStepResponse createFromPipelineStepWithStepNumber(PipelineStep step) {
        return new PipelineStepResponse(
                step.getStepId(),
                step.getProvider().getId(),
                step.getInputType(),
                step.getOutputType(),
                step.getParams(),
                step.getStepNumber()
        );
    }

    private PipelineStepResponse() {
    }

    private PipelineStepResponse(UUID stepId, UUID providerId, String inputType, String outputType,
                                 Map<String, Object> params) {
        this.stepId = stepId;
        this.providerId = providerId;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = params;
    }

    private PipelineStepResponse(UUID stepId, UUID providerId, String inputType, String outputType,
                                 Map<String, Object> params, Integer stepNumber) {
        this.stepId = stepId;
        this.providerId = providerId;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = params;
        this.stepNumber = stepNumber;
    }

    public UUID getStepId() {
        return stepId;
    }

    public UUID getProviderId() {
        return providerId;
    }

    public String getInputType() {
        return inputType;
    }

    public String getOutputType() {
        return outputType;
    }

    public Map<String, Object> getParams() {
        return params;
    }
}
