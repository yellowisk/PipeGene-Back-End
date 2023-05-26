package br.edu.ifsp.scl.pipegene.web.controller;

import br.edu.ifsp.scl.pipegene.configuration.security.AuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.usecases.execution.ExecutionTransaction;
import br.edu.ifsp.scl.pipegene.usecases.provider.ProviderService;
import br.edu.ifsp.scl.pipegene.web.model.provider.request.ProviderExecutionResultRequest;
import br.edu.ifsp.scl.pipegene.web.model.provider.request.ProviderRequest;
import br.edu.ifsp.scl.pipegene.web.model.provider.response.ProviderResponse;
import org.apache.coyote.Response;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1/providers")
public class ProviderController {

    private final ExecutionTransaction executionTransaction;
    private final ProviderService providerService;
    private final AuthenticationFacade authenticationFacade;

    public ProviderController(ExecutionTransaction executionTransaction, ProviderService providerService, AuthenticationFacade authenticationFacade) {
        this.executionTransaction = executionTransaction;
        this.providerService = providerService;
        this.authenticationFacade = authenticationFacade;
    }

    @PostMapping
    public ResponseEntity<ProviderResponse> addNewProvider(@RequestBody @Valid ProviderRequest providerRequest) {
        Provider provider = providerService.createNewProvider(providerRequest);

        return ResponseEntity.ok(ProviderResponse.createFromProvider(provider));
    }

    @GetMapping
    public ResponseEntity<List<ProviderResponse>> listAllProviders() {
        List<Provider> providers = providerService.listAllProviders();

        return ResponseEntity.ok(
                providers.stream()
                        .map(ProviderResponse::createFromProvider)
                        .collect(Collectors.toList())
        );
    }

    @GetMapping("{providerId}")
    public ResponseEntity<ProviderResponse> findProviderById(@PathVariable UUID providerId) {
        Provider provider = providerService.findProviderById(providerId);

        return ResponseEntity.ok(ProviderResponse.createFromProvider(provider));
    }

    @PatchMapping("/{providerId}")
    public ResponseEntity<ProviderResponse> updateProvider(
            @PathVariable UUID providerId,
            @RequestBody @Valid ProviderRequest providerRequest
    ) {
        Provider provider = providerService.updateProvider(providerId, providerRequest);

        return ResponseEntity.ok(ProviderResponse.createFromProvider(provider));
    }

    @PostMapping("/{providerId}/executions/{executionId}/steps/{stepId}")
    public ResponseEntity<?> notifyExecutionResult(
            @PathVariable UUID providerId,
            @PathVariable UUID executionId,
            @PathVariable UUID stepId,
            @RequestBody ProviderExecutionResultRequest providerExecutionResultRequest
    ) {

        executionTransaction.validateNotificationFromProvider(providerId, executionId, stepId);
        executionTransaction.processAsyncExecutionResult(providerId, executionId, stepId, providerExecutionResultRequest);

        return ResponseEntity.accepted().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<ProviderResponse>> getAllProvidersByUserId(){

        List<Provider> providers = providerService.listAllProvidersByUserId(authenticationFacade.getUserAuthenticatedId());

        return ResponseEntity.ok(providers.stream()
                .map(ProviderResponse::createFromProvider)
                .collect(Collectors.toList()));
    }
}

