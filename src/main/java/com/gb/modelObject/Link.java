package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Link {

    private Integer musicId;
    private String link;

    private static final Logger logger = LoggerFactory.getLogger(Link.class);

    public Link() {
    }

    public Link(Integer musicId, String link) {
        this.musicId = musicId;
        this.link = link;
    }

    public Integer getMusicId() {
        return musicId;
    }

    public void setMusicId(Integer musicId) {
        this.musicId = musicId;
    }

    public String getLink() {
        return link;
    }

    public void setLink(String link) {
        this.link = link;
    }

}
