package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Album {

    private Integer albumId;
    private String title;
    private Integer year;
    private Integer groupId;

    private static final Logger logger = LoggerFactory.getLogger(Album.class);

    public Album() {
    }

    public Album(Integer albumId, String title, Integer year, Integer groupId) {
        this.albumId = albumId;
        this.title = title;
        this.year = year;
        this.groupId = groupId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getGroupId() {
        return groupId;
    }

    public void setGroupId(Integer groupId) {
        this.groupId = groupId;
    }

}
