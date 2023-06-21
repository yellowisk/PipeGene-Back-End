package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;

import java.util.UUID;

public class ClonePipelineRequest {
    private UUID id;

    private UUID projectId;

    public ClonePipelineRequest() {}

    public UUID getProjectId() {return projectId;}

    public void setProjectId(UUID projectId) {this.projectId = projectId;}

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public Pipeline convertToPipeline() {
        return Pipeline.createWithOnlyId(id);
    }

}
