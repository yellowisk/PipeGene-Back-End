package br.edu.ifsp.scl.pipegene.external.persistence;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.domain.Provider;
import br.edu.ifsp.scl.pipegene.domain.ProviderOperation;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import br.edu.ifsp.scl.pipegene.usecases.provider.gateway.ProviderDAO;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLDataException;
import java.sql.SQLException;
import java.util.*;

@Repository
public class ProviderDAOImpl implements ProviderDAO {

    private final JdbcTemplate jdbcTemplate;
    private final ObjectMapper objectMapper;

    private final GroupDAO groupDAO;

    @Deprecated
    private final IAuthenticationFacade authentication;

    @Value("${queries.sql.provider-dao.select.provider-by-id}")
    private String selectProviderByIdQuery;

    @Value("${queries.sql.provider-dao.select.providers-by-ids}")
    private String selectProvidersByIdsQuery;

    @Value("${queries.sql.provider-dao.select.provider-all}")
    private String selectAllProvidersQuery;

    @Value("${queries.sql.provider-dao.insert.provider}")
    private String insertProviderQuery;

    @Value("${queries.sql.provider-dao.update.provider}")
    private String updateProviderQuery;

    @Value("${queries.sql.provider-dao.select.provider-all-by-userId}")
    private String selectAllProvidersByUserIdQuery;

    @Value("${queries.sql.group-provider-dao.insert.group-provider}")
    private String insertGroupProviderQuery;

    @Value("${queries.sql.group-provider-dao.select.group-id-by-provider-id}")
    private String selectGroupIdByProviderIdQuery;

    @Value("${queries.sql.group-provider-dao.exists.group-provider-by-group-id-and-provider-id}")
    private String existsGroupProviderByGroupAndProviderIdQuery;

    @Value("${queries.sql.group-provider-dao.select.group-id-by-provider-id}")
    private String selectAllGroupIdByProviderIdQuery;

    @Value("${queries.sql.provider-dao.select.provider-all-by-user-and-project-id}")
    private String selectAllProviderByUserAndProjectIdQuery;

    public ProviderDAOImpl(JdbcTemplate jdbcTemplate, ObjectMapper objectMapper, GroupDAO groupDAO, IAuthenticationFacade authentication) {
        this.jdbcTemplate = jdbcTemplate;
        this.objectMapper = objectMapper;
        this.groupDAO = groupDAO;
        this.authentication = authentication;
    }

    @Override
    public Optional<Provider> findProviderById(UUID id) {
        List<Provider> providers = jdbcTemplate.query(selectProviderByIdQuery, ps -> ps.setObject(1, id), this::mapperToProvider);

        return providers.isEmpty() ? Optional.empty() : Optional.of(providers.get(0));
    }

    @Override
    public List<Provider> findProvidersByIds(Collection<UUID> ids) {
        return jdbcTemplate.query(selectProvidersByIdsQuery,
                ps -> ps.setObject(1, ps.getConnection().createArrayOf("uuid", ids.toArray())),
                this::mapperToProvider);
    }

    @Override
    public List<Provider> findAllProviders() {
        return jdbcTemplate.query(selectAllProvidersQuery, this::mapperToProvider);
    }

    private Provider mapperToProvider(ResultSet rs, int rowNum) throws SQLException {
        UUID id = (UUID) rs.getObject("id");
        String name = rs.getString("name");
        String description = rs.getString("description");
        String url = rs.getString("url");
        String urlSource = rs.getString("url_source");
        Boolean isPublic = rs.getBoolean("public");

        String inputSupported = rs.getString("input_supported_types");
        List<String> inputSupportedTypes = Objects.isNull(inputSupported) ? Collections.emptyList()
                : Arrays.asList(inputSupported.split(","));

        String outputSupported = rs.getString("output_supported_types");
        List<String> outputSupportedTypes = Objects.isNull(outputSupported) ? Collections.emptyList()
                : Arrays.asList(outputSupported.split(","));

        List<UUID> groupsIds = jdbcTemplate.query(selectGroupIdByProviderIdQuery, ps -> ps.setObject(1, id),
                    (rs1, rowNum1) -> (UUID) rs1.getObject("group_id"));

        List<Group> groups = groupsIds.stream().map(groupDAO::findGroupById)
                .filter(Optional::isPresent)
                .map(Optional::get)
                .toList();


        try {
            String operationStr = rs.getString("operations");
            List<ProviderOperation> operations = Objects.isNull(operationStr) ? Collections.emptyList()
                    : objectMapper.readValue(operationStr, new TypeReference<>() {
            });

            return Provider.createWithAllValues(id, name, description, url, urlSource, isPublic, groups, inputSupportedTypes, outputSupportedTypes,
                    operations);
        } catch (JsonProcessingException e) {
            e.printStackTrace();
            throw new SQLDataException();
        }
    }

    @Override
    public Provider saveNewProvider(Provider provider) {
        String operations;
        try {
            operations = objectMapper.writeValueAsString(provider.getOperations());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }

        jdbcTemplate.update(insertProviderQuery, provider.getId(), provider.getName(), provider.getDescription(),
                provider.getUrl(), provider.getUrlSource(), provider.getPublic(), String.join(",", provider.getInputSupportedTypes()),
                String.join(",", provider.getOutputSupportedTypes()), operations, authentication.getUserAuthenticatedId());

        return provider;
    }

    @Override
    public Provider updateProvider(Provider provider) {
        UUID providerId = provider.getId();
        String operations;

        try {
            operations = objectMapper.writeValueAsString(provider.getOperations());
        } catch (JsonProcessingException e) {
            throw new IllegalArgumentException();
        }

        jdbcTemplate.update(updateProviderQuery, provider.getName(), provider.getDescription(), provider.getUrl(),
                provider.getPublic(),
                String.join(",", provider.getInputSupportedTypes()),
                String.join(",", provider.getOutputSupportedTypes()), operations, providerId);

        return provider;
    }

    @Override
    public List<Provider> findAllProvidersByUserId(UUID userId) {
        return jdbcTemplate.query(selectAllProvidersByUserIdQuery, this::mapperToProvider, userId, userId);
    }

    @Override
    public List<Provider> findAllProvidersByUserAndProjectId(UUID userId, UUID projectId) {
        return jdbcTemplate.query(selectAllProviderByUserAndProjectIdQuery, this::mapperToProvider, userId, projectId);
    }

    @Override
    public void createGroupProvider(UUID groupId, UUID providerId) {
        jdbcTemplate.update(insertGroupProviderQuery, groupId, providerId);
    }

    @Override
    public List<UUID> findAllGroupsByProviderId(UUID providerId) {
        return jdbcTemplate.query(selectAllGroupIdByProviderIdQuery, ps -> ps.setObject(1, providerId),
                (rs, rowNum) -> (UUID) rs.getObject("group_id"));
    }

    @Override
    public boolean existsGroupProvider(UUID groupId, UUID providerId) {
        return jdbcTemplate.queryForObject(existsGroupProviderByGroupAndProviderIdQuery, Boolean.class, groupId, providerId);
    }
}
