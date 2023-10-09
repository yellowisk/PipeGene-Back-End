package br.edu.ifsp.scl.pipegene.external.persistence.entities;

import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.domain.ProviderOperation;

import java.util.*;

public class ProviderEntity {
    private UUID id;
    private String name;
    private String description;
    private String url;
    private String urlSource;
    private Boolean isPublic;

    private List<Group> groups;
    private Set<String> inputSupportedTypes;
    private Set<String> outputSupportedTypes;
    private Set<ProviderOperation> operations;


    private ProviderEntity(UUID id, String name, String description, String url, String urlSource, Boolean isPublic, List<Group> groups, Collection<String> inputSupportedTypes,
                           Collection<String> outputSupportedTypes, Collection<ProviderOperation> operations) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.urlSource = urlSource;
        this.isPublic = isPublic;
        this.groups = groups;
        this.inputSupportedTypes = new HashSet<>(inputSupportedTypes);
        this.outputSupportedTypes = new HashSet<>(outputSupportedTypes);
        this.operations = new HashSet<>(operations);
    }

    public static ProviderEntity of(UUID id, String name, String description, String url, String urlSource, Boolean isPublic, List<Group> groups,
                                    Collection<String> inputSupportedTypes, Collection<String> outputSupportedTypes,
                                    Collection<ProviderOperation> operations) {
        return new ProviderEntity(id, name, description, url, urlSource, isPublic, groups, inputSupportedTypes, outputSupportedTypes, operations);
    }

    public static ProviderEntity createFromProviderWithoutId(Provider provider) {
        return new ProviderEntity(UUID.randomUUID(), provider.getName(), provider.getDescription(), provider.getUrl(),
                provider.getUrlSource(), provider.getPublic(), null, provider.getInputSupportedTypes(),
                provider.getOutputSupportedTypes(), provider.getOperations());
    }

    public Provider convertToProvider() {
        return Provider.createWithAllValues(id, name, description, url, urlSource, isPublic, groups, inputSupportedTypes, outputSupportedTypes, operations);
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
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

    public String getUrlSource() {
        return urlSource;
    }

    public void setUrlSource(String urlSource) {
        this.urlSource = urlSource;
    }

    public Set<ProviderOperation> getOperations() {
        return operations;
    }

    public void setOperations(Set<ProviderOperation> operations) {
        this.operations = operations;
    }

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void setGroups(List<Group> groups) {
        this.groups = groups;
    }
}
