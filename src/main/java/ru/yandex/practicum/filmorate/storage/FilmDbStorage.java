package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.exception.ValidationException;
import ru.yandex.practicum.filmorate.mappers.FilmRowMapper;
import ru.yandex.practicum.filmorate.mappers.GenreRowMapper;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Genre;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.Timestamp;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Repository
@Component
@Slf4j
@Qualifier("filmDbStorage")
public class FilmDbStorage extends BaseStorage<Film> implements FilmStorage {
    private static final int POPULAR_DEFAULT = 10;
    private static final String FIND_ALL_FILM_ID_QUERY = "SELECT id FROM films";
    private static final String FIND_FILM_BY_ID_QUERY = "SELECT films.ID, films.NAME, films.DESCRIPTION, films.RELEASE_DATE, films.DURATION, r.ID AS mpa, r.NAME AS mpa_name FROM films LEFT JOIN mpa AS r ON films.MPA = r.ID WHERE films.ID = ?";
    private static final String FIND_FILM_BY_NAME_QUERY = "SELECT f.ID, f.NAME, f.DESCRIPTION, f.RELEASE_DATE, f.DURATION, r.name AS mpa_name FROM films AS f JOIN mpa AS r ON f.mpa = r.ID WHERE f.NAME = ?";
    private static final String FIND_FILM_BY_ID_WITHOUT_MPA = "SELECT films.ID, films.NAME, films.DESCRIPTION, films.RELEASE_DATE, films.DURATION FROM films WHERE films.ID = ?";
    private static final String FIND_LIKES_OF_FILM = "SELECT l.user_id FROM likes AS l WHERE l.film_id = ?";
    private static final String INSERT_FILM_WITHOUT_MPA_QUERY = "INSERT INTO films (name, description, release_date, duration) VALUES (?, ?, ?, ?)";
    private static final String INSERT_FILM = "INSERT INTO films (name, description, release_date, duration, mpa) VALUES (?, ?, ?, ?, ?)";
    private static final String INSERT_GENRE_OF_FILM_QUERY = "INSERT INTO genre_of_film (film_id, genre_id) VALUES (?, (SELECT genre.id FROM genres WHERE name = ? LIMIT 1))";
    private static final String INSERT_GENRE_OF_FILM = "INSERT INTO genre_of_film (film_id, genre_id) VALUES (?, ?)";
    private static final String INSERT_LIKES_OF_FILM_QUERY = "INSERT INTO likes (film_id, user_id) VALUES (?, ?)";
    private static final String UPDATE_FILM_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = ? WHERE id = ?";
    private static final String UPDATE_FILM_WITHOUT_MPA_QUERY = "UPDATE films SET name = ?, description = ?, release_date = ?, duration = ?, mpa = null WHERE id = ?";
    private static final String DELETE_GENRES_OF_FILM_QUERY = "DELETE FROM genre_of_film WHERE film_id = ?";
    private static final String DELETE_LIKES_OF_FILM_QUERY = "DELETE FROM likes WHERE film_id = ?";
    private static final String FIND_POPULAR_FILMS_ID_QUERY = "SELECT likes.film_id FROM likes GROUP BY likes.film_id ORDER BY COUNT(likes.user_id) DESC LIMIT ?";
    private static final String FIND_GENRES = "SELECT * FROM genres";
    private static final String FIND_GENRE_BY_ID_QUERY = "SELECT * FROM genres WHERE id = ?";
    private static final String FIND_MPA = "SELECT * FROM mpa";
    private static final String FIND_MPA_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";
    private static final String FIND_GENRES_OF_FILM = "SELECT g.ID, g.TITLE FROM GENRE_OF_FILM AS gf JOIN GENRES AS g ON gf.GENRE_ID = g.ID WHERE FILM_ID = ?";
    private static final String FIND_MPA_OF_FILM_QUERY = "SELECT MPA.ID AS mpa, MPA.TITLE AS mpa_name FROM MPA JOIN FILMS ON MPA.ID = FILMS.MPA WHERE FILMS.ID = ?";
    private final GenreRowMapper genreRowMapper;
    private final MpaRowMapper mpaRowMapper;

    @Autowired
    public FilmDbStorage(JdbcTemplate jdbc, FilmRowMapper mapper, GenreRowMapper genreRowMapper, MpaRowMapper mpaRowMapper) {
        super(jdbc, mapper);
        this.genreRowMapper = genreRowMapper;
        this.mpaRowMapper = mpaRowMapper;
    }

    @Override
    public Collection<Film> getFilms() {
        List<Integer> listId = jdbc.queryForList(FIND_ALL_FILM_ID_QUERY, Integer.class);
        return listId.stream()
                .map(this::getFilm)
                .sorted(Comparator.comparing(Film::getId))
                .peek(film -> {
                    Set<Genre> genres = film.getGenres();
                    film.setGenres(new TreeSet<>(genres));
                })
                .collect(Collectors.toList());
    }

    @Override
    public Film getFilm(Integer id) {
        Optional<Film> optionalFilm = findOne(FIND_FILM_BY_ID_QUERY, id);
        Film film;
        if (optionalFilm.isPresent()) {
            film = optionalFilm.get();
        } else {
            throw new NoSuchElementException("Film with id " + id + " not found");
        }
        List<Genre> genres = jdbc.query(FIND_GENRES_OF_FILM, genreRowMapper, film.getId());
        film.setGenres(new TreeSet<>(genres));
        List<Integer> listLikes = jdbc.queryForList(FIND_LIKES_OF_FILM, Integer.class, id);
        Set<Integer> likes = new HashSet<>(listLikes);
        film.setLikes(likes);
        return film;
    }

    @Override
    public List<Film> getPopularFilms(Integer count) {
        if (count == null) count = POPULAR_DEFAULT;
        List<Integer> popularId = jdbc.queryForList(FIND_POPULAR_FILMS_ID_QUERY, Integer.class, count);
        return popularId.stream()
                .map(this::getFilm)
                .collect(Collectors.toList());
    }

    @Override
    public Film addFilm(Film film) {
        validate(film);
        validateGenreAndMpa(film);
        Integer id;
        if (!film.mpaIsNull()) {
            id = insert(INSERT_FILM, film.getName(), film.getDescription(), Timestamp.valueOf(film.getReleaseDate().atStartOfDay()), film.getDuration(), film.getMpa().getId());
        } else {
            id = insert(INSERT_FILM_WITHOUT_MPA_QUERY, film.getName(), film.getDescription(), Timestamp.valueOf(film.getReleaseDate().atStartOfDay()), film.getDuration());
        }
        Set<Genre> genres = film.getGenres();
        addGenres(genres, id);
        Set<Integer> likes = film.getLikes();
        addLikes(likes, id);
        film.setId(id);
        List<Genre> genresDb = jdbc.query(FIND_GENRES_OF_FILM, genreRowMapper, film.getId());
        film.setGenres(new TreeSet<>(genresDb));
        try {
            Mpa mpa = jdbc.queryForObject(FIND_MPA_BY_ID_QUERY, mpaRowMapper, film.getMpa().getId());
            film.setMpa(mpa);
        } catch (EmptyResultDataAccessException ignored) {
            film.setMpa(new Mpa());
        }
        log.info("Валидация прошла, фильм успешно добавлен");
        return film;
    }

    @Override
    public Film updateFilm(Film film) {
        if (film.getId() == null) {
            log.warn("Id должен быть указан");
            throw new NotFoundException("Id должен быть указан");
        }
        if (checkFilm(film.getId())) {
            validate(film);
            validateGenreAndMpa(film);
            if (!film.mpaIsNull()) {
                update(UPDATE_FILM_QUERY, film.getName(), film.getDescription(), Timestamp.valueOf(film.getReleaseDate().atStartOfDay()), film.getDuration(), film.getMpa().getId(), film.getId());
            } else {
                update(UPDATE_FILM_WITHOUT_MPA_QUERY, film.getName(), film.getDescription(), Timestamp.valueOf(film.getReleaseDate().atStartOfDay()), film.getDuration(), film.getId());
            }
            delete(DELETE_GENRES_OF_FILM_QUERY, film.getId());
            delete(DELETE_LIKES_OF_FILM_QUERY, film.getId());
            addGenres(film.getGenres(), film.getId());
            addLikes(film.getLikes(), film.getId());
            List<Genre> genresDb = jdbc.query(FIND_GENRES_OF_FILM, genreRowMapper, film.getId());
            film.setGenres(new TreeSet<>(genresDb));
            try {
                Mpa mpa = jdbc.queryForObject(FIND_MPA_BY_ID_QUERY, mpaRowMapper, film.getMpa().getId());
                film.setMpa(mpa);
            } catch (EmptyResultDataAccessException ignored) {
                film.setMpa(new Mpa());
            }
            log.info("Валидация прошла, фильм успешно обновлен");
            return film;
        } else {
            log.warn("Id не найден");
            throw new NotFoundException("Id не найден");
        }
    }

    @Override
    public void updatePopularFilm(Film film) {
    } // Метод был нужен в InMemoryFilmStorage, с добавлением БД бесполезен, но есть в интерфейсе FilmStorage

    private void validate(Film film) {
        if (film.getName() == null || film.getName().isBlank()) {
            log.warn("Название фильма не может быть пустым");
            throw new ValidationException("Название фильма не может быть пустым");
        }
        if (film.getDescription().length() > 200) {
            log.warn("Максимальная длина описания — 200 символов");
            throw new ValidationException("Максимальная длина описания — 200 символов");
        }
        if (film.getReleaseDate().isBefore(LocalDate.of(1895, 12, 28))) {
            log.warn("Дата релиза не может быть раньше 28 декабря 1895 года");
            throw new ValidationException("Дата релиза не может быть раньше 28 декабря 1895 года");
        }
        if (film.getDuration() <= 0) {
            log.warn("Продолжительность фильма должна быть положительной");
            throw new ValidationException("Продолжительность фильма должна быть положительной");
        }
    }

    private void addGenres(Set<Genre> genres, Integer id) {
        for (Genre genre : genres) {
            insert(INSERT_GENRE_OF_FILM, id, genre.getId());
        }
    }

    private void addLikes(Set<Integer> likes, Integer id) {
        for (Integer like : likes) {
            insert(INSERT_LIKES_OF_FILM_QUERY, id, like);
        }
    }

    private boolean checkFilm(Integer id) {
        Optional<Film> optionalFilm = findOne(FIND_FILM_BY_ID_QUERY, id);
        if (optionalFilm.isPresent()) {
            return true;
        } else {
            throw new NotFoundException("Film not found");
        }
    }

    private void validateGenreAndMpa(Film film) {
        Set<Genre> genres = film.getGenres();
        if (!genres.isEmpty()) {
            for (Genre genre : genres) {
                try {
                    jdbc.queryForObject(FIND_GENRE_BY_ID_QUERY, genreRowMapper, genre.getId());
                } catch (EmptyResultDataAccessException e) {
                    throw new NotFoundException("genre not found");
                }
            }
        }
        if (film.getMpa().getId() != null) {
            try {
                jdbc.queryForObject(FIND_MPA_BY_ID_QUERY, mpaRowMapper, film.getMpa().getId());
            } catch (EmptyResultDataAccessException e) {
                throw new NotFoundException("mpa not found");
            }
        }
    }
}
