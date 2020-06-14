package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Genre {

    private Integer genreId;
    private String name;

    private static final Logger logger = LoggerFactory.getLogger(Genre.class);

    public Genre() {
    }

    public Genre(Integer genreId, String name) {
        this.genreId = genreId;
        this.name = name;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

}
