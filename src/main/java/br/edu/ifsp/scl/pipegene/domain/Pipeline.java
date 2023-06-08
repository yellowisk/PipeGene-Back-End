package br.edu.ifsp.scl.pipegene.domain;

import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.PipelineStepDTO;

import java.util.*;

public class Pipeline {

    private UUID id;
    private Project project;
    private String description;
    private List<PipelineStep> steps;


    public void addStep(PipelineStep step) {
        step.setPipeline(this);
        steps.add(step);
    }

    public Pipeline(UUID id, Project project, String description, List<PipelineStep> steps) {
        this.id = id;
        this.project = project;
        this.description = description;
        this.steps = steps;

        steps.forEach(step -> step.setPipeline(this));
    }

    private Pipeline(UUID id, String description) {
        this.id = id;
        this.description = description;
        this.steps = new ArrayList<>();
    }

    public Pipeline(String description) {
        this.description = description;
    }

    public static Pipeline createWithoutProjectAndSteps(UUID id, String description) {
        return new Pipeline(id, description);
    }

    public static Pipeline createWithOnlyId(UUID id) {
        return new Pipeline(id);
    }

    public Pipeline(UUID id) {
        this.id = id;
    }

    public Pipeline(UUID id, Project project, String description, PipelineStep step) {
        this.id = id;
        this.project = project;
        this.description = description;
        this.steps = new ArrayList<>();
        steps.add(step);
    }

    public Pipeline getNewInstanceWithId(UUID uuid) {
        return new Pipeline(uuid, project, description, steps);
    }

    public static Pipeline getNewInstanceWithDescription(String description) {
        return new Pipeline(description);
    }

    public void setProject(Project project) {
        if (Objects.isNull(this.project)) {
            this.project = project;
        }
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

    public static Pipeline createWithoutId(Project project, String description, List<PipelineStep> steps) {
        return new Pipeline(null, project, description, steps);
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

    public void setSteps(List<PipelineStepDTO> updatedPipelineSteps) {
        steps.clear();
        updatedPipelineSteps.forEach(this::addStepDTO);
    }

    public void setPipelineSteps(List<PipelineStep> pipelineSteps) {
        this.steps = pipelineSteps;
    }
}
