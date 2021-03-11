package com.example.demo.dao;

import com.example.demo.model.JournalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("journalEntryPostgres")
public class JournalEntryDataAccessService implements JournalEntryDao{

    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public JournalEntryDataAccessService(JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public String insertJournalEntry(UUID id, JournalEntry journalEntry) {
        final String sql = "insert into journalEntry(id, title, markdown, html, userid) values (:id, :title, :markdown, :html, :userid)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("title", journalEntry.getTitle())
                .addValue("markdown", journalEntry.getMarkdown())
                .addValue("html", journalEntry.getHtml())
                .addValue("userid", journalEntry.getUserid());
        //.addValue("address", user.getPassword());
        namedParameterJdbcTemplate.update(sql, parameters);
        return "Entry" + journalEntry.getId() + "was added.";
    }

    @Override
    public List<JournalEntry> selectAllJournalEntries() {
        final String sql = "SELECT id, title, markdown, html, userid FROM journalEntry";
        return jdbcTemplate.query(sql, (resultSet, i) -> {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String title = resultSet.getString("title");
            String markdown = resultSet.getString("markdown");
            String html = resultSet.getString("html");
            UUID userid = UUID.fromString(resultSet.getString("userid"));
            return new JournalEntry(id, title, markdown, html, userid);
        });
    }

    @Override
    public List<JournalEntry> selectAllJournalEntriesByUserId(UUID userid) {
        final String sql = "SELECT id, title, markdown, html, userid FROM journalEntry WHERE userid = ?";
        return jdbcTemplate.query(sql, (resultSet, i) -> {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String title = resultSet.getString("title");
            String markdown = resultSet.getString("markdown");
            String html = resultSet.getString("html");
            return new JournalEntry(id, title, markdown, html, userid);
        });
    }

    @Override
    public Optional<JournalEntry> selectJournalEntryById(UUID id) {
        final String sql = "SELECT id, title, markdown, html FROM journalEntry WHERE id = ?";
        JournalEntry journalEntry = jdbcTemplate.queryForObject(
                sql,
                new Object[]{id},
                (resultSet, i) -> {
                    UUID journalEntryId = UUID.fromString(resultSet.getString("id"));
                    String title = resultSet.getString("title");
                    String markdown = resultSet.getString("markdown");
                    String html = resultSet.getString("html");
                    UUID userid = UUID.fromString(resultSet.getString("userid"));
                    return new JournalEntry(journalEntryId, title, markdown, html, userid);
                });
        return Optional.ofNullable(journalEntry);
    }

    @Override
    public String deleteJournalEntryById(UUID id) {
        final String sql = "DELETE FROM journalEntry WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
        return "Entry " + id.toString() + " was deleted";
    }

    @Override
    public String updateJournalEntryById(UUID id, JournalEntry journalEntry) {
        final String sql = "UPDATE journalEntry SET title = :title, markdown = :markdown, html = :html where id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("title", journalEntry.getTitle())
                .addValue("markdown", journalEntry.getMarkdown())
        .addValue("html", journalEntry.getHtml());
        namedParameterJdbcTemplate.update(sql, parameters);
        return "";
    }
    
    
}
