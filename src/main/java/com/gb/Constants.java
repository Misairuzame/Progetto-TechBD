package com.gb;

public class Constants {

    /**
     * Nomi relativi al database
     */
    public static final String DB_NAME = "MusicDB";
    public static final String MUSIC_TABLE = "music";
    public static final String MUSICID = "musicid";
    public static final String TITLE = "title";
    public static final String AUTHORID = "authorid";
    public static final String ALBUMID = "albumid";
    public static final String YEAR = "year";
    public static final String GENREID = "genreid";
    public static final String ALBUM_TABLE = "album";
    public static final String GROUPID = "groupid";
    public static final String ARTIST_TABLE = "artist";
    public static final String ARTISTID = "artistid";
    public static final String NAME = "name";
    public static final String GENRE_TABLE = "genre";
    public static final String GROUP_TABLE = "grouptable";
    public static final String LINK_TABLE = "link";
    public static final String LINK = "link";

    /**
     * Altre costanti
     */
    public static final String USER_DIR     = System.getProperty("user.dir");
    public static final String CONT_TYPE    = "content-type";
    public static final String DB_PATH      = "jdbc:postgresql://localhost:5432/MusicDBPostgres?currentSchema=MusicDB";
    public static final int    PAGE_SIZE    = 10;

    /**
     * HTTP METHODS
     */
    public static final String GET = "GET";
    public static final String PUT = "PUT";
    public static final String POST = "POST";
    public static final String DELETE = "DELETE";

}