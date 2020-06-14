package com.gb;

public class Constants {

    /**
     * Nomi relativi al database
     */
    public static final String DB_NAME = "MusicDB";
    public static final String MUSIC_TABLE = "music";
    public static final String M_ID = "MusicId";
    public static final String TITLE = "Title";
    public static final String AUTHORID = "AuthorId";
    public static final String ALBUMID = "AlbumId";
    public static final String YEAR = "Year";
    public static final String GENREID = "GenreId";

    /**
     * Altre costanti
     */
    public static final String USER_DIR     = System.getProperty("user.dir");
    public static final String BG_IMG       = USER_DIR+"\\src\\resources\\public\\background.webp";
    public static final String BG_IMG_NAME  = "background.webp";
    public static final String CONT_TYPE    = "content-type";
    public static final String SUCCESS      = "success";
    public static final String FAILURE      = "error";
    public static final String DB_PATH      = "jdbc:postgresql://localhost:5432/MusicDBPostgres?currentSchema=MusicDB";
    public static final int    PAGE_SIZE    = 10;

}