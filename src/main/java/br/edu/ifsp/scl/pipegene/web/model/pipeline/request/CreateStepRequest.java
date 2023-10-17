package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.PipelineStep;

import java.util.Map;
import java.util.UUID;

public class CreateStepRequest {
    private UUID providerId;
    private String inputType;
    private String outputType;
    private Map<String, Object> params;

    public CreateStepRequest(UUID providerId, String inputType, String outputType, Map<String, Object> params) {
        this.providerId = providerId;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = params;
    }

    public static CreateStepRequest createFromStep(PipelineStep step) {
        return new CreateStepRequest(step.getProvider().getId(), step.getInputType(),
                step.getOutputType(), step.getParams());
    }

    public CreateStepRequest() {}

    public UUID getProviderId() {
        return providerId;
    }

    public void setProviderId(UUID providerId) {
        this.providerId = providerId;
    }

    public String getInputType() {
        return inputType;
    }

    public void setInputType(String inputType) {
        this.inputType = inputType;
    }

    public String getOutputType() {
        return outputType;
    }

    public void setOutputType(String outputType) {
        this.outputType = outputType;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

}
