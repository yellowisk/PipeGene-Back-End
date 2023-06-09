package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.PipelineStep;
import br.edu.ifsp.scl.pipegene.domain.Provider;

import java.util.Map;
import java.util.UUID;

public class PipelineStepRequest {
    private UUID stepId;

    private UUID providerId;
    private String inputType;
    private String outputType;

    private Map<String, Object> params;
    private Integer stepNumber;



    public Provider convertToProvider() {
        return Provider.createWithIdAndInputOutputSupportedTypes(providerId, inputType, outputType);
    }

    public PipelineStepRequest() {
    }

    public PipelineStepRequest(UUID providerId, String inputType, String outputType, Map<String, Object> params, Integer stepNumber) {
        this.providerId = providerId;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = params;
        this.stepNumber = stepNumber;
    }

    public PipelineStepRequest(UUID stepId, UUID providerId, String inputType, String outputType, Map<String, Object> params, Integer stepNumber) {
        this.stepId = stepId;
        this.providerId = providerId;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = params;
        this.stepNumber = stepNumber;
    }

    public PipelineStep convertToPipelineStep() {
        return PipelineStep.of(stepId, Provider.createWithOnlyId(providerId), inputType, outputType, params, stepNumber);
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

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }
}
