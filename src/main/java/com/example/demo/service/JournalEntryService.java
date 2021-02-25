package com.example.demo.service;

import com.example.demo.dao.JournalEntryDao;
import com.example.demo.model.JournalEntry;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Service
public class JournalEntryService {
    
    private final JournalEntryDao journalEntryDao;

    @Autowired
    public JournalEntryService(@Qualifier("journalEntryPostgres") JournalEntryDao journalEntryDao) {
        this.journalEntryDao = journalEntryDao;
    }

    public int addJournalEntry(JournalEntry JournalEntry) {
        return journalEntryDao.insertJournalEntry(JournalEntry);
    }

    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryDao.selectAllJournalEntries();
    }

    public List<JournalEntry> getAllJournalEntriesByUserId(UUID userid) {
        return journalEntryDao.selectAllJournalEntriesByUserId(userid);
    }

    public Optional<JournalEntry> getJournalEntryById(UUID id) {
        return journalEntryDao.selectJournalEntryById(id);
    }

    public int deleteJournalEntry(UUID id){
        return journalEntryDao.deleteJournalEntryById(id);
    }

    public int updateJournalEntry(UUID id, JournalEntry newJournalEntry){
        return journalEntryDao.updateJournalEntryById(id, newJournalEntry);
    }
}
