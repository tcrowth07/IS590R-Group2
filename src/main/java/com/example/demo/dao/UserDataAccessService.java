package com.example.demo.dao;

import com.example.demo.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("UserPostgres")
public class UserDataAccessService implements UserDao {

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public UserDataAccessService(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;

    }

    @Override
    public String insertUser(UUID id, User user) {
        final String sql = "INSERT INTO ApplicationUser(id, name, email) values(:id, :name, :email)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", user.getName())
                .addValue("email", user.getEmail());
                //.addValue("address", user.getPassword());
        namedParameterJdbcTemplate.update(sql, parameters);
        return "User" + user.getName() + "was added.";
    }

    @Override
    public List<User> selectAllUsers() {
        final String sql = "SELECT * FROM ApplicationUser";
        return jdbcTemplate.query(sql, (resultSet, i) -> {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String name = resultSet.getString("name");
            String email = resultSet.getString("email"); //?
            //implement password and email stuff here too.
            return new User(id, name, email);
        });
    }

    @Override
    public Optional<User> selectUserById(UUID id) {
        final String sql = "SELECT * FROM ApplicationUser WHERE id = ?";
        User user = jdbcTemplate.queryForObject(
                sql,
                new Object[]{id},
                (resultSet, i) -> {
                    UUID userId = UUID.fromString(resultSet.getString("id"));
                    String name = resultSet.getString("name");
                    String email = resultSet.getString("email");
                    return new User(userId, name, email);
                });
        return Optional.ofNullable(user);
    }

    @Override
    public String deleteUserById(UUID id) {
        final String sql = "DELETE FROM ApplicationUser WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
        return "User" + id.toString() + "was added.";
    }

    @Override
    public User updateUserById(UUID id, User user) {
        final String sql = "UPDATE ApplicationUser SET name = :name, email = :email where id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("name", user.getName())
                .addValue("email", user.getEmail());
        namedParameterJdbcTemplate.update(sql, parameters);
        return user;
    }
}
