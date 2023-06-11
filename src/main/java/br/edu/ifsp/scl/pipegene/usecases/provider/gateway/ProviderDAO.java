package br.edu.ifsp.scl.pipegene.usecases.provider.gateway;

import br.edu.ifsp.scl.pipegene.domain.Provider;

import java.util.*;


public interface ProviderDAO {

    Optional<Provider> findProviderById(UUID id);

    List<Provider> findAllProviders();

    List<Provider> findProvidersByIds(Collection<UUID> ids);

    Provider saveNewProvider(Provider provider);

    Provider updateProvider(Provider provider);

    List<Provider> findAllProvidersByUserId(UUID userId);
    void createGroupProvider(UUID groupId, UUID providerId);

    boolean existsGroupProvider(UUID groupId, UUID providerId);

}
