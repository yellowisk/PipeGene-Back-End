package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;

import java.util.UUID;

public class ClonePipelineRequest {
    private UUID id;

    public ClonePipelineRequest() {
    }

    public ClonePipelineRequest(UUID id) {
        this.id = id;
    }

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
