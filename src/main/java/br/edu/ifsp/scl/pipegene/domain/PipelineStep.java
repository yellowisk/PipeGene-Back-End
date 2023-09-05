package br.edu.ifsp.scl.pipegene.domain;

import java.util.*;

public class PipelineStep {

    private UUID stepId;
    private Provider provider;
    private String inputType;
    private String outputType;
    private Map<String, Object> params;
    private Integer stepNumber;

    private Pipeline pipeline;

    private PipelineStep(UUID stepId, Provider provider, String inputType, String outputType,
                         Map<String, Object> params, Integer stepNumber, Pipeline pipeline) {
        this.stepId = stepId;
        this.provider = provider;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = Collections.unmodifiableMap(params);
        this.stepNumber = stepNumber;
        this.pipeline = pipeline;
    }

    private PipelineStep(String inputType, String outputType,
                         Map<String, Object> params) {
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = Collections.unmodifiableMap(params);
    }

    public PipelineStep(String inputType, String outputType, Map<String, Object> params, Integer stepNumber) {
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = params;
        this.stepNumber = stepNumber;
    }

    public static PipelineStep of(String inputType, String outputType, Map<String, Object> params) {
        return new PipelineStep(inputType, outputType, params);
    }

    public static PipelineStep of(String inputType, String outputType, Map<String, Object> params, Integer stepNumber) {
        return new PipelineStep(inputType, outputType, params, stepNumber);
    }

    public PipelineStep(UUID stepId, Provider provider, String inputType, String outputType,
                        Map<String, Object> params, Integer stepNumber) {
        this.stepId = stepId;
        this.provider = provider;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = Collections.unmodifiableMap(params);
        this.stepNumber = stepNumber;
    }

    public PipelineStep getNewInstanceWithOnlyId(UUID stepId) {
        return new PipelineStep(stepId, provider, inputType, outputType,
                params, stepNumber, pipeline);
    }

    public static PipelineStep of(UUID stepId, Provider providerId, String inputType, String outputType,
                                  Map<String, Object> params, Integer stepNumber, Pipeline pipeline) {
        return new PipelineStep(stepId, providerId, inputType, outputType, params, stepNumber, pipeline);
    }

    public static PipelineStep of(UUID stepId, Provider providerId, String inputType, String outputType,
                                  Map<String, Object> params, Integer stepNumber) {
        return new PipelineStep(stepId, providerId, inputType, outputType, params, stepNumber);
    }

    public static PipelineStep createGeneratingStepId(UUID providerId, String inputType, String outputType,
                                                      Map<String, Object> executionStepParams, Integer stepNumber) {
        Provider provider = Provider.createWithOnlyId(providerId);
        return new PipelineStep(UUID.randomUUID(), provider, inputType, outputType,
                Objects.requireNonNull(executionStepParams), stepNumber, null);
    }

    public static PipelineStep getNewInstanceOfStep(UUID pipelineId, String inputType, String outputType,
                                                    Map<String, Object> params,
                                                    Integer stepNumber) {
        return new PipelineStep(inputType, outputType, params, stepNumber);
    }

    public UUID getStepId() {
        return stepId;
    }

    public Provider getProvider() {
        return provider;
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

    public Pipeline getPipeline() {
        return pipeline;
    }

    public UUID getPipelineId() {
        return pipeline.getId();
    }

    public Integer getStepNumber() {
        return stepNumber;
    }

    public void setPipeline(Pipeline pipeline) {
        this.pipeline = pipeline;
    }

    public void setInputType(String type) {
        inputType = type;
    }
}