package br.edu.ifsp.scl.pipegene.usecases.pipeline;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.*;
import br.edu.ifsp.scl.pipegene.usecases.pipeline.gateway.PipelineDAO;
import br.edu.ifsp.scl.pipegene.usecases.project.gateway.ProjectDAO;
import br.edu.ifsp.scl.pipegene.usecases.provider.gateway.ProviderDAO;
import br.edu.ifsp.scl.pipegene.web.exception.GenericResourceException;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.*;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.CreateStepRequest;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
public class PipelineCRUDImpl implements PipelineCRUD {

    private final ProjectDAO projectDAO;
    private final ProviderDAO providerDAO;
    private final PipelineDAO pipelineDAO;
    private final IAuthenticationFacade authenticationFacade;

    public PipelineCRUDImpl(ProjectDAO projectDAO, ProviderDAO providerDAO, PipelineDAO pipelineDAO, IAuthenticationFacade authenticationFacade) {
        this.projectDAO = projectDAO;
        this.providerDAO = providerDAO;
        this.pipelineDAO = pipelineDAO;
        this.authenticationFacade = authenticationFacade;
    }

    @Override
    public Pipeline addNewPipeline(UUID projectId, CreatePipelineRequest request) {
        Optional<Project> optionalProject = projectDAO.findProjectById(projectId);

        if (optionalProject.isEmpty()) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }

        if (request.executionStepsIsEmpty()) {
            throw new GenericResourceException("Please, add one step", "Invalid Pipeline Request");
        }

        List<PipelineStepRequest> steps = request.getSteps();
        validatePipelineSteps(steps, projectId);

        Project project = optionalProject.get();
        List<PipelineStep> pipelineSteps = mapToPipelineStep(steps);
        Pipeline pipeline = Pipeline.createWithoutId(project, request.getDescription(), PipelineStatus.ENABLED, pipelineSteps);

        return pipelineDAO.savePipeline(pipeline);
    }

    private void validatePipelineSteps(List<PipelineStepRequest> steps, UUID projectId) {
        Set<UUID> providersIds = steps.stream().map(PipelineStepRequest::getProviderId).collect(Collectors.toSet());

        Map<UUID, Provider> providersMap = providerDAO.findProvidersByIds(providersIds).stream()
                .collect(Collectors.toMap(Provider::getId, Function.identity()));

        Project project = projectDAO.findProjectById(projectId).orElseThrow(
                () -> new ResourceNotFoundException("Not found project with id: " + projectId)
        );

        UUID projectGroupId = project.getGroupId();

        if (providersMap.size() < providersIds.size()) {
            throw new GenericResourceException("Please, verify provider id, inputType and outputType", "Invalid Pipeline Request");
        }

        for (int i = 0; i < steps.size(); i++) {

            PipelineStepRequest step = steps.get(i);
            Provider provider = providersMap.get(step.getProviderId());

            if (!provider.getPublic() && !providerDAO.existsGroupProvider(projectGroupId, provider.getId())){
                throw new GenericResourceException("Please, provider doesn't have permission to this project", "Invalid Pipeline Request");
            }

            if ((i != 0) && !provider.isInputSupportedType(step.getInputType())) {
                throw new GenericResourceException("Please, verify provider id, inputType and outputType", "Invalid Pipeline Request");
            }

            if (!provider.isOutputSupportedType(step.getOutputType())) {
                throw new GenericResourceException("Please, verify provider id, inputType and outputType", "Invalid Pipeline Request");
            }

            // if it has next step
            if (i+ 1 < steps.size()) {
                PipelineStepRequest nextStep = steps.get(i + 1);
                Provider nextProvider = providersMap.get(nextStep.getProviderId());

                if (!nextProvider.isInputSupportedType(step.getOutputType())) {
                    throw new GenericResourceException("Please, verify provider id, inputType and outputType", "Invalid Pipeline Request");
                }
            }

        }
    }

    public void validatePipelineStepsFromUpdate(List<PipelineStep> steps) {

        List<Integer> reqStepNumbers = steps.stream()
                .map(PipelineStep::getStepNumber).sorted()
                .toList();

        steps.forEach(step -> {
            if (reqStepNumbers.stream().filter(stepNumber -> stepNumber.equals(step.getStepNumber())).count() > 1) {
                throw new GenericResourceException("Please, verify step numbers", "Invalid Pipeline Request");
            }
        });

        for (int i = 1; i < reqStepNumbers.size(); i++) {
            if (reqStepNumbers.get(i) != reqStepNumbers.get(i - 1) + 1) {
                throw new GenericResourceException("Please, verify step numbers", "Invalid Pipeline Request");
            }
        }
    }

    private List<PipelineStep> mapToPipelineStep(List<PipelineStepRequest> pipelineStepRequest) {
        AtomicInteger atomicInteger = new AtomicInteger(1);
        return pipelineStepRequest
                .stream()
                .map(e -> PipelineStep.createGeneratingStepId(e.getProviderId(), e.getInputType(), e.getOutputType(),
                        e.getParams(), atomicInteger.getAndIncrement()))
                .collect(Collectors.toList());
    }

    @Override
    public List<Pipeline> findAllPipeline(UUID projectId) {
        return pipelineDAO.findAll(projectId);
    }

    @Override
    public List<Pipeline> listAllPipelinesByUserId(UUID userId) {
        if (!userId.equals(authenticationFacade.getUserAuthenticatedId())) {
            throw new IllegalArgumentException();
        }

        return pipelineDAO.listAllByOwnerId(userId);
    }

    @Override
    public Pipeline findByProjectIdAndPipelineId(UUID projectId, UUID pipelineId) {
        Boolean projectExists = projectDAO.projectExists(projectId);
        if (!projectExists) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }

        Optional<Pipeline> opt = pipelineDAO.findPipelineById(pipelineId);

        if (opt.isEmpty()) {
            throw new ResourceNotFoundException("Not found pipeline with id: " + pipelineId);
        }

        return opt.get();
    }

    @Override
    public List<Pipeline> listAllPipelineByProviderIdAndProject(UUID projectId, UUID providerId) {
        if(!projectDAO.projectExists(projectId)) {
            throw new ResourceNotFoundException("Couldn't find project with id: " + projectId);
        }

        if (!providerDAO.isProviderInProject(providerId, projectId)) {
            throw new ResourceNotFoundException("Couldn't find provider " + providerId + " in project " + projectId);
        }

        return pipelineDAO.listAllPipelineByProjectAndProvider(projectId, providerId);
    }

    @Override
    public List<Pipeline> listAllPipelineByProviderId(UUID providerId) {
        providerDAO.findProviderById(providerId).orElseThrow(
                () -> new ResourceNotFoundException("Couldn't find provider with id: " + providerId)
        );

        return pipelineDAO.listAllPipelinesByProvider(providerId);
    }

    @Override
    public PipelineStepDTO findPipelineStepById(UUID projectId, UUID pipelineId, UUID stepId) {
        Boolean projectExists = projectDAO.projectExists(projectId);
        if (!projectExists) {
            throw new ResourceNotFoundException("Couldn't find project with id: " + projectId);
        }

        Boolean pipelineExists = pipelineDAO.pipelineExists(pipelineId);
        if (!pipelineExists) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        return pipelineDAO.findPipelineStepById(pipelineId);
    }

    @Override
    public List<PipelineStepDTO> listAllPipelineStepsByPipelineId(UUID projectId, UUID pipelineId) {
        Boolean projectExists = projectDAO.projectExists(projectId);
        if (!projectExists) {
            throw new ResourceNotFoundException("Couldn't find project with id: " + projectId);
        }

        Boolean pipelineExists = pipelineDAO.pipelineExists(pipelineId);
        if (!pipelineExists) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        return pipelineDAO.findAllPipelineStepsByPipelineId(pipelineId);
    }

    @Override
    public Pipeline updatePipeline(UUID projectId, UUID pipelineId, UpdatePipelineRequest request) {

        if (!pipelineDAO.pipelineExists(pipelineId)) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        if (projectDAO.findProjectById(projectId).isEmpty()) {
            throw new ResourceNotFoundException("Not found project with id: " + projectId);
        }

        if (request.getSteps().isEmpty()) {
            throw new GenericResourceException("Please, add one step", "Invalid Pipeline Request");
        }

        validatePipelineSteps(request.getSteps(), projectId);
        Pipeline reqPipeline = request.convertToPipeline();
        validatePipelineStepsFromUpdate(reqPipeline.getSteps());

        List<UUID> reqStepIds = reqPipeline.getSteps().stream()
                .map(PipelineStep::getStepId)
                .toList();

        List<UUID> dbStepIds = pipelineDAO.findPipelineById(pipelineId).get()
                .getSteps().stream().map(PipelineStep::getStepId)
                .toList();

        List<UUID> stepsToRemove = dbStepIds.stream()
                .filter(stepId -> !reqStepIds.contains(stepId))
                .toList();

        for (UUID stepId : stepsToRemove) {
            deletePipelineStep(projectId, pipelineId, stepId);
        }

        List<PipelineStep> stepsAdded = reqPipeline.getSteps().stream()
                .filter(step -> step.getStepId() == null).toList();
        List<PipelineStep> newSteps = new ArrayList<>(reqPipeline.getSteps());

        newSteps.removeAll(stepsAdded);
        reqPipeline.setSteps(newSteps);

        pipelineDAO.updatePipeline(reqPipeline.getNewInstanceWithId(pipelineId));
        stepsAdded.forEach(stepToAdd -> addNewPipelineStep(pipelineId, CreateStepRequest.createFromStep(stepToAdd)));

        return reqPipeline.getNewInstanceWithId(pipelineId);
    }

    @Override
    public Pipeline addNewPipelineStep(UUID pipelineId, CreateStepRequest request) {
        Boolean pipelineExists = pipelineDAO.pipelineExists(pipelineId);
        if (!pipelineExists) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        List<PipelineStepDTO> stepsDTO = pipelineDAO.findAllPipelineStepsByPipelineId(pipelineId);
        List<PipelineStep> steps = PipelineStepDTO.createListFromPipelineStepDTOList(stepsDTO);
        Provider provider = providerDAO.findProviderById(request.getProviderId()).get();

        if (!provider.isInputSupportedType(request.getInputType())) {
            throw new GenericResourceException("Please, verify provider id, inputType and outputType", "Invalid Pipeline Request");
        }

        if (!provider.isOutputSupportedType(request.getOutputType())) {
            throw new GenericResourceException("Please, verify provider id, inputType and outputType", "Invalid Pipeline Request");
        }

        PipelineStep step = PipelineStep.createGeneratingStepId(request.getProviderId(), request.getInputType(),
                request.getOutputType(), request.getParams(), steps.size() + 1);

        return pipelineDAO.addPipelineStep(pipelineId, step);
    }

    public void validatePipelineCloning(UUID exportProjectId, UUID importProjectId, UUID pipelineId) {
        if (!projectDAO.projectExists(exportProjectId)) {
            throw new ResourceNotFoundException("Couldn't find project with id: " + exportProjectId);
        }

        if (!projectDAO.projectExists(importProjectId)) {
            throw new ResourceNotFoundException("Couldn't find project with id: " + importProjectId);
        }

        if (!pipelineDAO.pipelineExists(pipelineId)) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }
    }

    @Override
    public Pipeline clonePipeline(UUID exportProjectId, UUID pipelineId, ClonePipelineRequest request) {
        UUID importProjectId = request.getProjectId();

        validatePipelineCloning(exportProjectId, importProjectId, pipelineId);

        UUID projectGroupId = projectDAO.findProjectById(importProjectId).get().getGroupId();

        //Pipeline that will be copied and exported
        Pipeline pipeline = pipelineDAO.findFullPipelineById(exportProjectId, pipelineId).get();

        Pipeline imported = Pipeline.createWithoutId(
                Project.createWithId(importProjectId),
                pipeline.getDescription()  + " [Imported]", pipeline.getStatus(), pipeline.getSteps()
        );

        /*Verifying connection between providers and the group of the project that's importing
        the pipeline*/
        for (int i = 0; i < imported.getSteps().size(); i++) {

            PipelineStep step = imported.getSteps().get(i);
            Provider provider = providerDAO.findProviderById(step.getProvider().getId()).get();

            if (!provider.getPublic() && !providerDAO.existsGroupProvider(projectGroupId, provider.getId())) {
                providerDAO.createGroupProvider(projectGroupId, provider.getId());
            }
        }

        return pipelineDAO.clonePipeline(imported);
    }

    @Override
    public Pipeline deletePipelineStep(UUID projectId, UUID pipelineId, UUID pipelineStepId) {

        Boolean pipelineExists = pipelineDAO.pipelineExists(pipelineId);
        if (!pipelineExists) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        Pipeline pipeline = pipelineDAO.findPipelineById(pipelineId).get();
        PipelineStep chosenStep = null;

        List<PipelineStepDTO> pipelineStepsDTO = listAllPipelineStepsByPipelineId(projectId, pipeline.getId());
        List<PipelineStep> pipelineSteps = PipelineStepDTO.createListFromPipelineStepDTOList(pipelineStepsDTO);
        List<PipelineStep> pipelineStepsResponse = new ArrayList<>();

        if (!pipelineSteps.isEmpty()) {
            for (PipelineStep step : pipelineSteps) {
                if (step.getStepId().equals(pipelineStepId)) {
                    chosenStep = step;
                } else {
                    pipelineStepsResponse.add(step);
                }
            }
        } else {
            throw new ResourceNotFoundException("Pipeline " + pipelineId + " is empty");
        }

        return pipelineDAO.deletePipelineStep(pipelineStepsResponse, chosenStep.getStepId());
    }

    @Override
    public Pipeline disablePipeline(UUID projectId, UUID pipelineId) {
        if (!projectDAO.isOwner(projectId, authenticationFacade.getUserAuthenticatedId())) {
            throw new GenericResourceException("User " + authenticationFacade.getUserAuthenticatedId() +
                    " is not the owner of the project " + projectId, "Invalid user");
        }

        Pipeline pipeline = findByProjectIdAndPipelineId(projectId, pipelineId);
        pipeline.setStatus(PipelineStatus.DISABLED);

        return pipelineDAO.disablePipeline(pipeline);
    }

}
