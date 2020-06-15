package com.gb.DAO;

import com.gb.modelObject.Group;

import java.util.List;

public interface GroupDAO {

    List<Group> getAllGroups();

    int insertGroup(Group group);

}
