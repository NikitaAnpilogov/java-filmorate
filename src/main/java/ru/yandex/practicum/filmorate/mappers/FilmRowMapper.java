package ru.yandex.practicum.filmorate.mappers;

import lombok.NoArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;
import ru.yandex.practicum.filmorate.model.Film;
import ru.yandex.practicum.filmorate.model.Mpa;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;

@NoArgsConstructor
@Component
public class FilmRowMapper implements RowMapper<Film> {
    @Override
    public Film mapRow(ResultSet rs, int rowNum) throws SQLException {
        Film film = new Film();
        film.setId(rs.getInt("id"));
        film.setName(rs.getString("name"));
        film.setDescription(rs.getString("description"));
        Timestamp date = rs.getTimestamp("release_date");
        film.setReleaseDate(date.toLocalDateTime().toLocalDate());
        film.setDuration(rs.getInt("duration"));
        Integer idMpa = rs.getInt("mpa");
        String mpa = rs.getString("mpa_name");
        if (mpa != null) {
            film.setMpa(new Mpa(idMpa, mpa));
        }
        return film;
    }
}
