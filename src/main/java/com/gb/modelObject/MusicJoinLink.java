package com.gb.modelObject;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.sql.ResultSet;
import java.sql.SQLException;

import static com.gb.Constants.*;
import static com.gb.Constants.GENREID;

public class MusicJoinLink {
    private static final Logger logger = LoggerFactory.getLogger(MusicJoinLink.class);
    private Music music;
    private Link link;

    public MusicJoinLink(ResultSet rs) {
        try {
            music = new Music();
            music.setMusicId(rs.getInt(MUSICID));
            music.setTitle(rs.getString(TITLE));
            music.setAuthorId(rs.getInt(AUTHORID));
            music.setAlbumId(rs.getInt(ALBUMID));
            music.setYear(rs.getInt(YEAR));
            music.setGenreId(rs.getInt(GENREID));
            link = new Link();
            link.setMusicId(rs.getInt(MUSICID));
            link.setLink(rs.getString(LINK));
        } catch(SQLException e) {
            logger.error("Error creating MusicJoinLink object: {}", e.getMessage());
        }
    }

    public Music getMusic() {
        return music;
    }

    public Link getLink() {
        return link;
    }
}
