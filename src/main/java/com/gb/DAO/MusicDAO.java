package com.gb.DAO;

import com.gb.modelObject.Music;
import com.gb.modelObject.SearchFilter;

import java.util.List;

public interface MusicDAO {

    /**
     * GET /music
     * GET su collezione --> Restituisce l'intera collezione
     */
    List<Music> getAllMusic(int page);

    /**
     * GET /music/:id
     * GET su risorsa --> Restituisce una rappresentazione della risorsa
     */
    List<Music> getMusicById(long id);

    /**
     * GET /music/joinall
     * Join su tutti gli ID
     */
    List<Music> getMusicJoinAll(long id);

    /**
     * GET /music?params
     * GET su collezione con parametri --> Restituisce una rappresentazione
     * delle risorse con determinati parametri.
     */
    List<Music> getMusicByParams(SearchFilter filter, int page);

    /**
     * PUT /music/:id
     * PUT su risorsa --> Aggiorna la risorsa
     */
    int updateOneMusic(Music music);

    /**
     * POST /music
     * POST su collezione --> Inserisce un elemento nella collezione
     */
    int insertMusic(Music music);

    /**
     * DELETE /music/:id
     * DELETE su risorsa --> Elimina la risorsa
     */
    int deleteMusic(int id);

}
