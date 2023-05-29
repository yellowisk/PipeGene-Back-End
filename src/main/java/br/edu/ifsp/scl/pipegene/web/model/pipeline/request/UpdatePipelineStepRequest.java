package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.PipelineStep;

import java.util.Map;
import java.util.UUID;

public class UpdatePipelineStepRequest {
    private UUID pipelineId;
    private String inputType;
    private String outputType;
    private Map<String, Object> params;
    private Integer stepNumber;

    public UpdatePipelineStepRequest() {
    }

    //Check its need, logic's too complex
    public PipelineStep convertToPipelineStep() {
        return PipelineStep.getNewInstanceOfStep(pipelineId, inputType, outputType, params, stepNumber);
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

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setStepNumber(Integer stepNumber) {
        this.stepNumber = stepNumber;
    }

    public UUID getPipelineID() {
        return pipelineId;
    }

    public void setPipelineID(UUID pipelineID) {
        this.pipelineId = pipelineID;
    }

}
