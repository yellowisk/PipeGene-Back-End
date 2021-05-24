package br.edu.ifsp.scl.pipegene.web.model.execution.request;

import java.util.UUID;

public class ExecutionRequestFlowDetails {

    private UUID providerId;
    private String inputType;
    private String outputType;

    public ExecutionRequestFlowDetails() {
    }

    public ExecutionRequestFlowDetails(UUID providerId, String inputType, String outputType) {
        this.providerId = providerId;
        this.inputType = inputType;
        this.outputType = outputType;
    }


    @Override
    public String toString() {
        return "ExecutionRequestFlowDetails{" +
                "providerId=" + providerId +
                ", inputType='" + inputType + '\'' +
                ", outputType='" + outputType + '\'' +
                '}';
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
}