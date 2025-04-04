package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.model.Genre;

import java.util.List;
import java.util.Optional;

@Repository
@Component
@Slf4j
public class GenreDbStorage extends BaseStorage<Genre> implements GenreStorage {
    private static final String FIND_ALL_GENRES_QUERY = "SELECT * FROM genres";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String INSERT_GENRE_QUERY = "INSERT INTO genres (title) VALUES(?)";

    public GenreDbStorage(JdbcTemplate jdbcTemplate, GenreRowMapper mapper) {
        super(jdbcTemplate, mapper);
    }

    public List<Genre> getGenres() {
        return findMany(FIND_ALL_GENRES_QUERY);
    }

    public Genre getGenre(Integer id) {
        Optional<Genre> result = findOne(FIND_GENRE_BY_ID_QUERY, id);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new NotFoundException("Genre with id " + id + " not found");
        }
    }

    public Genre addGenre(Genre genre) {
        Integer id = insert(INSERT_GENRE_QUERY, genre.getName());
        genre.setId(id);
        return genre;
    }
}
