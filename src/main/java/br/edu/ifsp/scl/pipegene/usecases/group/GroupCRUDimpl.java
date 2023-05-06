package br.edu.ifsp.scl.pipegene.usecases.group;

import br.edu.ifsp.scl.pipegene.configuration.security.IAuthenticationFacade;
import br.edu.ifsp.scl.pipegene.domain.Group;
import br.edu.ifsp.scl.pipegene.usecases.group.gateway.GroupDAO;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class GroupCRUDimpl implements GroupCRUD{

    private final GroupDAO groupDAO;

    private final IAuthenticationFacade authentication;

    public GroupCRUDimpl(GroupDAO groupDAO, IAuthenticationFacade authentication) {
        this.groupDAO = groupDAO;
        this.authentication = authentication;
    }

    @Override
    public Group addNewGroup(String name, String description) {
        Group group = Group.createWithoutGroupParticipations(UUID.randomUUID(), name, description, authentication.getUserAuthenticatedId());
        return groupDAO.saveGroup(group);
    }
}
