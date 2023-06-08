package br.edu.ifsp.scl.pipegene.domain;

import java.util.*;

public class Provider {

    private UUID id;
    private String name;
    private String description;
    private String url;
    private Boolean isPublic;
    private List<Group> groups;
    private List<String> inputSupportedTypes;
    private List<String> outputSupportedTypes;
    private List<ProviderOperation> operations;

    public static Provider createWithAllValues(UUID id, String name, String description, String url, Boolean isPublic, List<Group> groups,
                                               Collection<String> inputSupportedTypes, Collection<String> outputSupportedTypes,
                                               Collection<ProviderOperation> operations) {
        return new Provider(id, name, description, url, isPublic, groups, inputSupportedTypes, outputSupportedTypes, operations);
    }

    public static Provider createWithoutIdAndGroups(String name, String description, String url,
                                                    boolean isPublic,
                                                    Collection<String> inputSupportedTypes,
                                                    Collection<String> outputSupportedTypes,
                                                    Collection<ProviderOperation> operations) {
        return new Provider(null, name, description, url, isPublic, null, inputSupportedTypes, outputSupportedTypes, operations);
    }

    public static Provider createWithIdAndInputOutputSupportedTypes(UUID id, String inputSupportedType, String outputSupportedType) {
        return new Provider(id, Collections.singletonList(inputSupportedType), Collections.singletonList(outputSupportedType));
    }

    public static Provider createWithOnlyId(UUID id) {
        return new Provider(id, new ArrayList<>(), new ArrayList<>());
    }

    public static Provider createWithPartialValues(UUID id, String name, String description) {
        return new Provider(id, name, description);
    }

    public static Provider createWithNameAndDescription(String name, String description) {
        return new Provider(name, description);
    }

    private Provider(UUID id, String name, String description) {
        this.id = id;
        this.name = name;
        this.description = description;
    }

    private Provider(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public static Provider createWithPartialValues(UUID id, String name, String description,
                                                   List<String> inputSupportedTypes,
                                                   List<String> outputSupportedTypes) {
        return new Provider(id, name, description, inputSupportedTypes, outputSupportedTypes);
    }

    private Provider(UUID id, String name, String description, List<String> inputSupportedTypes,
                     List<String> outputSupportedTypes) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.inputSupportedTypes = inputSupportedTypes;
        this.outputSupportedTypes = outputSupportedTypes;
    }

    public boolean isInputSupportedType(String inputType) {
        return inputSupportedTypes.contains(inputType);
    }

    public boolean isOutputSupportedType(String outputType) {
        return outputSupportedTypes.contains(outputType);
    }

    private Provider(UUID id, List<String> inputSupportedType, List<String> outputSupportedType) {
        this.id = id;
        this.inputSupportedTypes = inputSupportedType;
        this.outputSupportedTypes = outputSupportedType;
    }

    private Provider(UUID id, String name, String description, String url, Boolean isPublic,
                     List<Group> groups, Collection<String> inputSupportedTypes,
                     Collection<String> outputSupportedTypes, Collection<ProviderOperation> operations) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.url = url;
        this.isPublic = isPublic;
        this.groups = groups;
        this.inputSupportedTypes = Objects.requireNonNull(List.copyOf(inputSupportedTypes));
        this.outputSupportedTypes = Objects.requireNonNull(List.copyOf(outputSupportedTypes));
        this.operations = Objects.requireNonNull(List.copyOf(operations));
    }

    public UUID getId() {
        return id;
    }

    public Provider getNewInstanceWithId(UUID id) {
        return new Provider(id, name, description, url, isPublic, groups, inputSupportedTypes, outputSupportedTypes, operations);
    }

    public String getUrl() {
        return url;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
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

    public Boolean getPublic() {
        return isPublic;
    }

    public void setPublic(Boolean aPublic) {
        isPublic = aPublic;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public List<Group> getGroups() {
        return groups;
    }

    public void addToGroup(Group group) {
        this.groups.add(group);
    }

    public void addListToGroup(List<Group> groups){
        this.groups.addAll(groups);
    }
}
