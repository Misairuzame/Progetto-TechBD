package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

public class JoinAll {
    private static final Logger logger = LoggerFactory.getLogger(JoinAll.class);

    private int musicId;
    private String musicTitle;
    private String groupName;
    private int numArtists;
    private String albumTitle;
    private int year;
    private String genreName;
    private int numLinks;

    public JoinAll(ResultSet rs) {
        try {
            this.musicId = rs.getInt("musicid");
            this.musicTitle = rs.getString("musictitle");
            this.groupName = rs.getString("groupname");
            this.numArtists = rs.getInt("numartisti");
            this.albumTitle = rs.getString("albumtitle");
            this.year = rs.getInt("year");
            this.genreName = rs.getString("genrename");
            this.numLinks = rs.getInt("numlink");
        } catch (SQLException e) {
            logger.error("Error creating JoinAll object: {}", e.getMessage());
        }
    }

    public int getMusicId() {
        return musicId;
    }

    public String getMusicTitle() {
        return musicTitle;
    }

    public String getGroupName() {
        return groupName;
    }

    public int getNumArtists() {
        return numArtists;
    }

    public String getAlbumTitle() {
        return albumTitle;
    }

    public int getYear() {
        return year;
    }

    public String getGenreName() {
        return genreName;
    }

    public int getNumLinks() {
        return numLinks;
    }
}
