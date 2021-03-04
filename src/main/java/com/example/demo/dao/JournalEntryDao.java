package com.example.demo.dao;

import com.example.demo.model.JournalEntry;

import java.util.List;
import java.util.Optional;
import java.util.UUID;


public interface JournalEntryDao {
    
    String insertJournalEntry(UUID id, JournalEntry je);

    default String insertJournalEntry(JournalEntry je) {
        UUID id = UUID.randomUUID();
        return insertJournalEntry(id, je);
    }

    List<JournalEntry> selectAllJournalEntries();

    List<JournalEntry> selectAllJournalEntriesByUserId(UUID userid);

    Optional<JournalEntry> selectJournalEntryById(UUID id);

    String deleteJournalEntryById(UUID id);

    String updateJournalEntryById(UUID id, JournalEntry je);

    //String changeMarkdownToHtml(UUID id, JournalEntry je);
    
}
