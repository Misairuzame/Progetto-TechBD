package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class Group {

    private Integer groupId;
    private String name;

    private static final Logger logger = LoggerFactory.getLogger(Group.class);

    public Group() {
    }

    public Group(ResultSet rs) {
        try {
            setGroupId(rs.getInt(GROUPID));
            setName(rs.getString(NAME));
        } catch(SQLException e) {
            logger.error("Error creating Group object: {}", e.getMessage());
        }
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
