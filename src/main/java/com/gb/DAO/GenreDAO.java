package com.gb.DAO;

import com.gb.modelObject.Genre;

import java.util.List;

public interface GenreDAO {

    List<Genre> getAllGenres();

    Genre getGenreById(int genreId);

    int insertGenre(Genre genre);

}
