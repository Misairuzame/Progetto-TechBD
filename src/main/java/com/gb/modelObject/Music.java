package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;

public class Music {

    private static final Logger logger = LoggerFactory.getLogger(Music.class);

    private Integer musicId;
    private String title;
    private Integer authorId;
    private Integer albumId;
    private Integer year;
    private Integer genreId;

    public Music() { }

    public Music(ResultSet rs) {
        try {
            setMusicId(rs.getInt(MUSICID));
            setTitle(rs.getString(TITLE));
            setAuthorId(rs.getInt(AUTHORID));
            setAlbumId(rs.getInt(ALBUMID));
            setYear(rs.getInt(YEAR));
            setGenreId(rs.getInt(GENREID));
        } catch(SQLException e) {
            logger.error("Error creating Music object: {}", e.getMessage());
        }
    }

    public Music(Integer musicId, String title, Integer authorId, Integer albumId, Integer year, Integer genreId) {
        this.musicId = musicId;
        this.title = title;
        this.authorId = authorId;
        this.albumId = albumId;
        this.year = year;
        this.genreId = genreId;
    }

    public Integer getMusicId() {
        return musicId;
    }

    public void setMusicId(Integer musicId) {
        this.musicId = musicId;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public Integer getAuthorId() {
        return authorId;
    }

    public void setAuthorId(Integer authorId) {
        this.authorId = authorId;
    }

    public Integer getAlbumId() {
        return albumId;
    }

    public void setAlbumId(Integer albumId) {
        this.albumId = albumId;
    }

    public Integer getYear() {
        return year;
    }

    public void setYear(Integer year) {
        this.year = year;
    }

    public Integer getGenreId() {
        return genreId;
    }

    public void setGenreId(Integer genreId) {
        this.genreId = genreId;
    }

}
