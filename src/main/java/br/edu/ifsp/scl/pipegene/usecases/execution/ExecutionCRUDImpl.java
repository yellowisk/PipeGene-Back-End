package br.edu.ifsp.scl.pipegene.usecases.execution;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.*;
import br.edu.ifsp.scl.pipegene.usecases.execution.gateway.ExecutionDAO;
import br.edu.ifsp.scl.pipegene.usecases.execution.queue.QueueService;
import br.edu.ifsp.scl.pipegene.usecases.project.gateway.ProjectDAO;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import br.edu.ifsp.scl.pipegene.web.model.execution.request.CreateExecutionRequest;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class ExecutionCRUDImpl implements ExecutionCRUD {

    private final ExecutionDAO executionDAO;
    private final ProjectDAO projectDAO;
    private final QueueService queueService;
    private final IAuthenticationFacade authenticationFacade;

    public ExecutionCRUDImpl(ExecutionDAO executionDAO, ProjectDAO projectDAO, QueueService queueService, IAuthenticationFacade authenticationFacade) {
        this.executionDAO = executionDAO;
        this.projectDAO = projectDAO;
        this.queueService = queueService;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public Execution addNewExecution(UUID projectId, CreateExecutionRequest request) {
        Optional<Project> optionalProject = projectDAO.findProjectById(projectId);
        if (optionalProject.isEmpty()) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }
        Project project = optionalProject.get();

        if (!project.hasDataset(request.getDatasetId())) {
            throw new ResourceNotFoundException("Not found dataset with id: " + projectId);
        }

        if (!project.hasPipeline(request.getPipelineId())) {
            throw new ResourceNotFoundException("Not found pipeline with id: " + projectId);
        }

        UUID executionId = queueService.add(request);
        Dataset dataset = project.findDatasetById(request.getDatasetId());
        Pipeline pipeline = project.findPipelineById(request.getPipelineId());

        if (pipeline.getStatus() == PipelineStatus.DISABLED) {
            throw new IllegalArgumentException("Pipeline " + pipeline.getId() + " is disabled");
        }

        pipeline.setFirstInputType(dataset.getFileType());

        Execution execution = Execution.createWithWaitingStatus(executionId, pipeline, dataset,
                request.getDescription());

        return executionDAO.saveExecution(execution);
    }

    @Override
    public Execution findExecutionById(UUID projectId, UUID executionId) {
        Boolean projectExists = projectDAO.projectExists(projectId);
        if (!projectExists) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }

        Optional<Execution> opt = executionDAO.findExecutionByProjectIdAndExecutionId(projectId, executionId);

        if (opt.isEmpty()) {
            throw new ResourceNotFoundException("Not found execution with id: " + executionId);
        }

        return opt.get();
    }

    @Override
    public List<Execution> listAllExecutionsByProjectId(UUID projectId) {
        Boolean projectExists = projectDAO.projectExists(projectId);
        if (!projectExists) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }

        return executionDAO.findAllExecutionsByProjectId(projectId);
    }

    @Override
    public List<Execution> listAllExecutionByUserId(UUID userId) {
        if (!userId.equals(authenticationFacade.getUserAuthenticatedId())) {
            throw new IllegalArgumentException();
        }

        return executionDAO.listAllByOwnerId(userId);
    }
}
