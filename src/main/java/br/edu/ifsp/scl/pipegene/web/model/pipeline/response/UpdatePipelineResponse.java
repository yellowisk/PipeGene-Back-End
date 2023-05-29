package br.edu.ifsp.scl.pipegene.web.model.pipeline.response;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;

import java.util.UUID;

public class UpdatePipelineResponse {
    private UUID id;
    private UUID projectId;
    private String description;

    public UpdatePipelineResponse(UUID id, UUID projectId, String description) {
        this.projectId = projectId;
        this.description = description;
    }

    public UpdatePipelineResponse() {
    }

    public UUID getId() {
        return id;
    }

    public UUID getProjectId() {
        return projectId;
    }

    public String getDescription() {
        return description;
    }

    public static UpdatePipelineResponse createFromPipeline(Pipeline p) {
        return new UpdatePipelineResponse(p.getId(), null, p.getDescription());
    }

}
