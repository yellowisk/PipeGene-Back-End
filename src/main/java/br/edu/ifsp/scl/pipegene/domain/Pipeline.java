package br.edu.ifsp.scl.pipegene.domain;

import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.PipelineStepDTO;

import java.util.*;

public class Pipeline {

    private UUID id;
    private Project project;
    private String description;
    private PipelineStatus status;
    private List<PipelineStep> steps;

    public Pipeline(UUID id, Project project, String description, PipelineStatus status, List<PipelineStep> steps) {
        this.id = id;
        this.project = project;
        this.description = description;
        this.status = status;
        this.steps = steps;

        steps.forEach(step -> step.setPipeline(this));
    }

    private Pipeline(UUID id, String description, PipelineStatus status) {
        this.id = id;
        this.description = description;
        this.status = status;
        this.steps = new ArrayList<>();
    }

    public Pipeline(String description) {
        this.description = description;
    }

    public static Pipeline createWithoutProjectAndSteps(UUID id, String description, PipelineStatus status) {
        return new Pipeline(id, description, status);
    }

    public static Pipeline createWithoutSteps(UUID id, Project project, String description, PipelineStatus status) {
        return new Pipeline(id, project, description, status, new ArrayList<>());
    }

    public static Pipeline createWithOnlyId(UUID id) {
        return new Pipeline(id);
    }

    public Pipeline(UUID id) {
        this.id = id;
    }

    public Pipeline(UUID id, Project project, String description,
                    PipelineStatus status, PipelineStep step) {
        this.id = id;
        this.project = project;
        this.description = description;
        this.status = status;
        this.steps = new ArrayList<>();
        steps.add(step);
    }

    public Pipeline(String description, PipelineStatus status, List<PipelineStep> steps) {
        this.description = description;
        this.status = status;
        this.steps = steps;
    }

    public Pipeline getNewInstanceWithId(UUID uuid) {
        return new Pipeline(uuid, project, description, status, steps);
    }

    public static Pipeline getNewInstanceWithDescriptionAndStatusAndSteps(String description, PipelineStatus status, List<PipelineStep> steps) {
        return new Pipeline(description, status, steps);
    }

    public static Pipeline createWithoutId(Project project, String description, PipelineStatus status, List<PipelineStep> steps) {
        return new Pipeline(null, project, description, status, steps);
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return project.getId();
    }

    public Project getProject() {
        return project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public PipelineStatus getStatus() {
        return status;
    }

    public void setStatus(PipelineStatus status) {
        this.status = status;
    }

    public List<PipelineStep> getSteps() {
        return steps;
    }

    public void sortedSteps() {
        steps.sort(Comparator.comparing(PipelineStep::getStepNumber));
    }

    public void setFirstInputType(String fileType) {
        PipelineStep step = steps.get(0);

        if (!step.getProvider().isInputSupportedType(fileType)) {
            throw new IllegalArgumentException();
        }

        step.setInputType(fileType);
    }

    public void setProject(Project project) {
        if (Objects.isNull(this.project)) {
            this.project = project;
        }
    }

    public void addStep(PipelineStep step) {
        step.setPipeline(this);
        steps.add(step);
    }

    public void addStepDTO(PipelineStepDTO stepDTO) {
        Provider provider = stepDTO.getProvider();
        PipelineStep step = PipelineStep.of(
                stepDTO.getStepId(),
                provider,
                stepDTO.getInputType(),
                stepDTO.getOutputType(),
                stepDTO.getParams(),
                stepDTO.getStepNumber(),
                this
        );
        addStep(step);
    }

    public void setSteps(List<PipelineStep> updatedPipelineSteps) {
        steps = Collections.emptyList();
        steps = Collections.unmodifiableList(updatedPipelineSteps);
    }

}
