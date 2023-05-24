package br.edu.ifsp.scl.pipegene.usecases.provider;

import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.web.model.provider.request.ProviderRequest;

import java.util.List;
import java.util.UUID;

public interface ProviderService {

    List<Provider> listAllProviders();

    Provider createNewProvider(ProviderRequest providerRequest);

    Provider findProviderById(UUID providerId);

    Provider updateProvider(UUID projectId, ProviderRequest providerRequest);

    List<Provider> listAllProvidersByUserId(UUID userId);

}
