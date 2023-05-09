package br.edu.ifsp.scl.pipegene.web.model.provider.request;

import br.edu.ifsp.scl.pipegene.domain.ProviderOperation;

import java.util.List;
import java.util.UUID;

public class ProviderUpdateRequest {
    private UUID id;
    private String name;
    private String description;
    private String url;
    private List<String> inputSupportedTypes;
    private List<String> outputSupportedTypes;
    private List<ProviderOperation> operations;

    public ProviderUpdateRequest() {
    }

    public UUID getId() {
        return id;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public String getUrl() {
        return url;
    }

    public List<String> getInputSupportedTypes() {
        return inputSupportedTypes;
    }

    public List<String> getOutputSupportedTypes() {
        return outputSupportedTypes;
    }

    public List<ProviderOperation> getOperations() {
        return operations;
    }
}
