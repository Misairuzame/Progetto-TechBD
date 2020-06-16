package com.gb.DAO;

import com.gb.modelObject.Artist;

import java.util.List;

public interface ArtistDAO {

    List<Artist> getAllArtists();

    Object artistJoinGroup();

    int updateArtist(Artist artist);

}
