package br.edu.ifsp.scl.pipegene.web.model.provider.request;

import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.web.model.project.ProjectUpdateRequest;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;

import javax.validation.constraints.NotNull;
import java.util.List;
import java.util.Set;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProviderRequest {

    @NotNull
    private String name;

    @NotNull
    private String description;

    @NotNull
    //@URL
    private String url;

    @JsonProperty("isPublic")
    private Boolean isPublic;

    private String urlSource;

    @NotNull
    private Set<String> inputSupportedTypes;

    @NotNull
    private Set<String> outputSupportedTypes;
    @NotNull
    private Set<ProviderOperationDTO> operations;

    private List<UUID> selectedProjectIds;
    public ProviderRequest() {
    }

    public Provider convertToProvider() {
        return Provider.createWithoutIdAndGroups(name, description, url, urlSource, isPublic, inputSupportedTypes, outputSupportedTypes,
                operations.stream()
                        .map(ProviderOperationDTO::convertToProviderOperation)
                        .collect(Collectors.toList())
        );
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Set<String> getInputSupportedTypes() {
        return inputSupportedTypes;
    }

    public void setInputSupportedTypes(Set<String> inputSupportedTypes) {
        this.inputSupportedTypes = inputSupportedTypes;
    }

    public Set<String> getOutputSupportedTypes() {
        return outputSupportedTypes;
    }

    public void setOutputSupportedTypes(Set<String> outputSupportedTypes) {
        this.outputSupportedTypes = outputSupportedTypes;
    }

    public Set<ProviderOperationDTO> getOperations() {
        return operations;
    }

    public void setOperations(Set<ProviderOperationDTO> operations) {
        this.operations = operations;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public String getUrlSource() {
        return urlSource;
    }

    public void setUrlSource(String urlSource) {
        this.urlSource = urlSource;
    }

    public List<UUID> getSelectedProjectIds() {
        return selectedProjectIds;
    }

    public void setSelectedProjectIds(List<UUID> selectedProjectIds) {
        this.selectedProjectIds = selectedProjectIds;
    }
}
