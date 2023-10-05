package br.edu.ifsp.scl.pipegene.usecases.pipeline;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.PipelineStep;
import br.edu.ifsp.scl.pipegene.domain.Pipeline;
import br.edu.ifsp.scl.pipegene.domain.Project;
import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.usecases.pipeline.gateway.PipelineDAO;
import br.edu.ifsp.scl.pipegene.usecases.project.gateway.ProjectDAO;
import br.edu.ifsp.scl.pipegene.usecases.provider.gateway.ProviderDAO;
import br.edu.ifsp.scl.pipegene.web.exception.GenericResourceException;
import br.edu.ifsp.scl.pipegene.web.exception.ResourceNotFoundException;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.request.*;
import br.edu.ifsp.scl.pipegene.web.model.pipeline.response.CreateStepRequest;
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
        Pipeline pipeline = Pipeline.createWithoutId(project, request.getDescription(), pipelineSteps);

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
                throw new GenericResourceException("Please, provider dont have permission to this project", "Invalid Pipeline Request");
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
    public List<PipelineStepDTO> findAllPipelineStepsByPipelineId(UUID pipelineId) {
        Boolean pipelineExists = pipelineDAO.pipelineExists(pipelineId);
        if (!pipelineExists) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        return pipelineDAO.findAllPipelineStepsByPipelineId(pipelineId);
    }

    @Override
    public Pipeline updatePipeline(UUID pipelineId, UpdatePipelineRequest request) {

        if (!pipelineDAO.pipelineExists(pipelineId)) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        Pipeline reqPipeline = request.convertToPipeline();
        Pipeline dbPipeline = pipelineDAO.findPipelineById(pipelineId).get();

        reqPipeline.getSteps().forEach(step -> System.out.println("requisition: " + step.getStepNumber() + " " + step.getStepId()));
        dbPipeline.getSteps().forEach(step -> System.out.println("database: " + step.getStepNumber() + " " + step.getStepId()));

        if(reqPipeline.getSteps().size() != dbPipeline.getSteps().size()) {
            for (int i = 0; i < reqPipeline.getSteps().size(); i++) {
                if (reqPipeline.getSteps().get(i).getStepId() != (dbPipeline.getSteps().get(i).getStepId())) {
                    deletePipelineStep(pipelineId, dbPipeline.getSteps().get(i).getStepId());
                    dbPipeline.getSteps().remove(i);
                }
            }
        }

        dbPipeline.getSteps().forEach(step -> System.out.println(step.getStepNumber() + " " + step.getStepId()));

        List<Provider> providers = providerDAO.findAllProviders();
        for (Provider provider : providers) {
            for (int i = 0; i < reqPipeline.getSteps().size(); i++) {
                if (provider.getId().equals(reqPipeline.getSteps().get(i).getProvider().getId())) {
                    if (!provider.isInputSupportedType(reqPipeline.getSteps().get(i).getInputType())) {
                        throw new GenericResourceException("Please, verify provider id, inputType and outputType", "Invalid Pipeline Request");
                    }
                    if (!provider.isOutputSupportedType(reqPipeline.getSteps().get(i).getOutputType())) {
                        throw new GenericResourceException("Please, verify provider id, inputType and outputType", "Invalid Pipeline Request");
                    }
                }
            }
        }

        return pipelineDAO.updatePipeline(reqPipeline.getNewInstanceWithId(pipelineId));
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

    @Override
    public Pipeline clonePipeline(UUID exportProjectId, UUID pipelineId, ClonePipelineRequest request) {
        UUID importProjectId = request.getProjectId();

        Boolean pipelineExists = pipelineDAO.pipelineExists(pipelineId);
        if (!pipelineExists) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        if (!projectDAO.projectExists(exportProjectId)) {
            throw new ResourceNotFoundException("Couldn't find project with id: " + exportProjectId);
        }

        if (!projectDAO.projectExists(importProjectId)) {
            throw new ResourceNotFoundException("Couldn't find project with id: " + importProjectId);
        }

        UUID projectGroupId = projectDAO.findProjectById(importProjectId).get().getGroupId();

        //Pipeline that will be copied and exported
        Pipeline pipeline = pipelineDAO.findFullPipelineById(exportProjectId, pipelineId).get();

        Pipeline imported = Pipeline.createWithoutId(
                Project.createWithId(importProjectId),
                pipeline.getDescription()  + " [Importado]", pipeline.getSteps()
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
    public Pipeline deletePipelineStep(UUID pipelineId, UUID pipelineStepId) {

        Boolean pipelineExists = pipelineDAO.pipelineExists(pipelineId);
        if (!pipelineExists) {
            throw new ResourceNotFoundException("Couldn't find pipeline with id: " + pipelineId);
        }

        Pipeline pipeline = pipelineDAO.findPipelineById(pipelineId).get();
        PipelineStep chosenStep = null;

        List<PipelineStepDTO> pipelineStepsDTO = findAllPipelineStepsByPipelineId(pipeline.getId());
        List<PipelineStep> pipelineSteps = PipelineStepDTO.createListFromPipelineStepDTOList(pipelineStepsDTO);
        List<PipelineStep> pipelineStepsResponse = new ArrayList<>();

        if (pipelineSteps.size() != 0) {
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

        return pipelineDAO.deletePipeline(pipelineStepsResponse, chosenStep.getStepId());
    }
}
