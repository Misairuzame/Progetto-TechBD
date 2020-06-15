package com.gb.DAO;

import com.gb.modelObject.Album;

import java.util.List;

public interface AlbumDAO {

    List<Album> getAllAlbums();

    Album getAlbumById(int albumId);

    int deleteAlbum(int albumId);

    int insertAlbum(Album album);

    Object albumJoinGroup();

}
