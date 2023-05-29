package br.edu.ifsp.scl.pipegene.web.model.pipeline.request;

import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.domain.Project;

import java.util.UUID;

public class UpdatePipelineRequest {
    private UUID pipelineId;

    private String description;

    private Project project;


    public UpdatePipelineRequest() { }

    public Pipeline convertToPipeline() {
        return Pipeline.getNewInstanceWithDescription(description);
    }

    public Project getProject() {
        return project;
    }

    public void setProject(Project project) {
        this.project = project;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
