package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class ArtistJoinGroup {
    private static final Logger logger = LoggerFactory.getLogger(ArtistJoinGroup.class);
    private Artist artist;
    private Group group;

    public ArtistJoinGroup(ResultSet rs) {
        try {
            artist = new Artist();
            artist.setArtistId(rs.getInt(ARTISTID));
            artist.setName(rs.getString(2));
            artist.setGroupId(rs.getInt(GROUPID));
            group = new Group();
            group.setGroupId(rs.getInt(GROUPID));
            group.setName(rs.getString(4));
        } catch (SQLException e) {
            logger.error("Error creating ArtistJoinGroup object: {}", e.getMessage());
        }
    }

    public Artist getArtist() {
        return artist;
    }

    public Group getGroup() {
        return group;
    }
}
