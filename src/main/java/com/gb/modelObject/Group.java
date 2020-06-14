package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Group {

    private Integer groupId;
    private String name;

    private static final Logger logger = LoggerFactory.getLogger(Group.class);

    public Group() {
    }

    public Group(Integer groupId, String name) {
        this.groupId = groupId;
        this.name = name;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
