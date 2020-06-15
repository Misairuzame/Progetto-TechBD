package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class Genre {

    private Integer genreId;
    private String name;

    private static final Logger logger = LoggerFactory.getLogger(Genre.class);

    public Genre() {
    }

    public Genre(ResultSet rs) {
        try {
            setGenreId(rs.getInt(GENREID));
            setName(rs.getString(NAME));
        } catch(SQLException e) {
            logger.error("Error creating Genre object: {}", e.getMessage());
        }
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
