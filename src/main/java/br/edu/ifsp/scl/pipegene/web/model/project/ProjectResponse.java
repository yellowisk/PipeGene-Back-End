package br.edu.ifsp.scl.pipegene.web.model.project;

import br.edu.ifsp.scl.pipegene.domain.Project;
import br.edu.ifsp.scl.pipegene.web.model.DatasetDTO;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.response.PipelineResponse;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public class ProjectResponse {

    private UUID id;
    private String name;
    private String description;
    private UUID groupId;

    private List<DatasetDTO> datasets;
    private List<PipelineResponse> pipelines;

    private ProjectResponse() {
    }

    public ProjectResponse(UUID id, String name, String description, UUID groupId, List<DatasetDTO> datasets,
                           List<PipelineResponse> pipelines) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.groupId = groupId;
        this.datasets = datasets;
        this.pipelines = pipelines;
    }

    public ProjectResponse(UUID id, String name, String description, UUID groupId) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.groupId = groupId;
    }

    public static ProjectResponse createFromProject(Project project) {
        return new ProjectResponse(
                project.getId(),
                project.getName(),
                project.getDescription(),
                project.getGroupId(),
                project.getDatasets().stream().map(DatasetDTO::createFromDataset).collect(Collectors.toList()),
                project.getPipelines().stream()
                        .map(PipelineResponse::createFromPipelineWithoutProject).collect(Collectors.toList())
        );
    }

    public UUID getId() {
        return id;
    }

    public List<DatasetDTO> getDatasets() {
        return datasets;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }

    public List<PipelineResponse> getPipelines() {
        return pipelines;
    }

    public UUID getGroupId() {
        return groupId;
    }
}
