package com.example.demo.dao;

import com.example.demo.model.JournalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository("journalEntryPostgres")
public class JournalEntryDataAccessService implements JournalEntryDao{

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public JournalEntryDataAccessService(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public int insertJournalEntry(UUID id, JournalEntry journalEntry) {
        return 0;
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
    public int deleteJournalEntryById(UUID id) {
        return 0;
    }

    @Override
    public int updateJournalEntryById(UUID id, JournalEntry journalEntry) {
        return 0;
    }
    
    
}
