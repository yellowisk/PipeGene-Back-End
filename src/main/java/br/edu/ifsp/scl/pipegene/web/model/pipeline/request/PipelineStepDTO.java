package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.PipelineStep;
import br.edu.ifsp.scl.pipegene.domain.Provider;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class PipelineStepDTO {
    private UUID stepId;
    private Provider provider;
    private String inputType;
    private String outputType;
    private Map<String, Object> params;
    private Integer stepNumber;

    public PipelineStepDTO(UUID stepId, Provider provider, String inputType,
                           String outputType, Map<String, Object> params,
                           Integer stepNumber) {
        this.stepId = stepId;
        this.provider = provider;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = params;
        this.stepNumber = stepNumber;
    }

    public PipelineStepDTO(UUID stepId, String inputType, String outputType, Map<String, Object> params, Integer stepNumber) {
        this.stepId = stepId;
        this.inputType = inputType;
        this.outputType = outputType;
        this.params = params;
        this.stepNumber = stepNumber;
    }

    public PipelineStepDTO() {
    }

    public static List<PipelineStepDTO> convertToPipelineStepDTOs(List<PipelineStep> pipelineSteps) {
        List<PipelineStepDTO> stepDTOs = new ArrayList<>();

        for (PipelineStep pipelineStep : pipelineSteps) {
            UUID stepId = pipelineStep.getStepId();
            Provider provider = pipelineStep.getProvider();
            String inputType = pipelineStep.getInputType();
            String outputType = pipelineStep.getOutputType();
            Map<String, Object> params = pipelineStep.getParams();
            Integer stepNumber = pipelineStep.getStepNumber();

            PipelineStepDTO stepDTO = new PipelineStepDTO(stepId, provider, inputType, outputType, params, stepNumber);
            stepDTOs.add(stepDTO);
        }

        return stepDTOs;
    }

    public static List<PipelineStep> createListFromPipelineStepDTOList(List<PipelineStepDTO> pipelineStepDTOList) {
        List<PipelineStep> pipelineSteps = new ArrayList<>();

        for (PipelineStepDTO pipelineStepDTO : pipelineStepDTOList) {
            UUID stepId = pipelineStepDTO.getStepId();
            Provider provider = pipelineStepDTO.getProvider();
            String inputType = pipelineStepDTO.getInputType();
            String outputType = pipelineStepDTO.getOutputType();
            Map<String, Object> params = pipelineStepDTO.getParams();
            Integer stepNumber = pipelineStepDTO.getStepNumber();

            PipelineStep pipelineStep = new PipelineStep(stepId, provider, inputType, outputType, params, stepNumber);
            pipelineSteps.add(pipelineStep);
        }

        return pipelineSteps;
    }

    public UUID getStepId() {
        return stepId;
    }

    public void setStepId(UUID stepId) {
        this.stepId = stepId;
    }

    public Provider getProvider() {
        return provider;
    }

    public void setProvider(Provider provider) {
        this.provider = provider;
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
}
