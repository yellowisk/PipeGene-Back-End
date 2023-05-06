package br.edu.ifsp.scl.pipegene.usecases.group;

import br.edu.ifsp.scl.pipegene.domain.Group;

public interface GroupCRUD {

    Group addNewGroup(String name, String description);

}
