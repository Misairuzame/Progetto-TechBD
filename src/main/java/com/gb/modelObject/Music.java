package com.gb.modelObject;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

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
            setMusicId(rs.getInt(1));
            setTitle(rs.getString(2));
            setAuthorId(rs.getInt(3));
            setAlbumId(rs.getInt(4));
            setYear(rs.getInt(5));
            setGenreId(rs.getInt(6));
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

    public JSONObject getJson() {
        /*
        TODO
        return new JSONObject()
                .put("id", getId())
                .put("title", getTitle())
                .put("author", getAuthor())
                .put("album", getAlbum())
                .put("year", getYear())
                .put("genre", getGenre())
                .put("url", getUrl());
         */
        return null;
    }



}
