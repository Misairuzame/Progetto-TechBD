package com.gb.DAO;

import com.gb.modelObject.Music;

import java.util.List;

public interface MusicDAO {

    List<Music> getAllMusic(int page);

    int updateMusic(Music music);

    int insertMusic(Music music);

    int deleteMusic(int id);

    //TODO: Decidere se implementare o meno
    List<Music> getMusicJoinAll(long id);

}
