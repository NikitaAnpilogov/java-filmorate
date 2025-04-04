package ru.yandex.practicum.filmorate.storage;

import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;
import ru.yandex.practicum.filmorate.exception.NotFoundException;
import ru.yandex.practicum.filmorate.mappers.MpaRowMapper;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.util.List;
import java.util.Optional;

@Repository
@Component
@Slf4j
public class MpaDbStorage extends BaseStorage<Mpa> implements MpaStorage {
    private static final String FIND_ALL_MPA_QUERY = "SELECT * FROM mpa";
    private static final String FIND_MPA_BY_ID_QUERY = "SELECT * FROM mpa WHERE id = ?";
    private static final String INSERT_MPA_QUERY = "INSERT INTO mpa (name) VALUES (?)";

    public MpaDbStorage(JdbcTemplate jdbcTemplate, MpaRowMapper mapper) {
        super(jdbcTemplate, mapper);
    }

    public List<Mpa> getListMpa() {
        return findMany(FIND_ALL_MPA_QUERY);
    }

    public Mpa getMpa(Integer id) {
        Optional<Mpa> result = findOne(FIND_MPA_BY_ID_QUERY, id);
        if (result.isPresent()) {
            return result.get();
        } else {
            throw new NotFoundException("Mpa with id " + id + " not found");
        }
    }

    public Mpa addMpa(Mpa mpa) {
        Integer id = insert(INSERT_MPA_QUERY, mpa.getName());
        mpa.setId(id);
        return mpa;
    }
}
