package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Artist {

    private Integer artistId;
    private String name;
    private Integer groupId;

    private static final Logger logger = LoggerFactory.getLogger(Artist.class);

    public Artist() {
    }

    public Artist(Integer artistId, String name, Integer groupId) {
        this.artistId = artistId;
        this.name = name;
        this.groupId = groupId;
    }

    public Integer getArtistId() {
        return artistId;
    }

    public void setArtistId(Integer artistId) {
        this.artistId = artistId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

}
