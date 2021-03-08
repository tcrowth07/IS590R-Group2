package com.example.demo.api;

import com.example.demo.model.JournalEntry;
import com.example.demo.service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequestMapping("api/v1/journalentry")
@RestController

//NEED JOURNAL SECURITY

public class JournalEntryController {

    private final JournalEntryService journalEntryService;

    @Autowired
    public JournalEntryController(JournalEntryService journalEntryService) {
        this.journalEntryService = journalEntryService;
    }

    @PostMapping
    public void addJournalEntry(@RequestBody JournalEntry journalEntry) {
        journalEntryService.addJournalEntry(journalEntry);
    }

    @GetMapping
    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryService.getAllJournalEntries();
    }

    @GetMapping(path = "{userid}")
    public List<JournalEntry> getAllJournalEntriesByUserId(@PathVariable("userid") UUID userid){
        return journalEntryService.getAllJournalEntriesByUserId(userid);
    }

    @GetMapping(path = "{id}")
    public JournalEntry getJournalEntryById(@PathVariable("id") UUID id){
        return journalEntryService.getJournalEntryById(id).orElse(null);
    }

    @DeleteMapping(path = "{id}")
    public void deleteJournalEntryById(@PathVariable UUID id){
        journalEntryService.deleteJournalEntry(id);
    }

    @PutMapping(path = "{id}")
    public void updateJournalEntry(@PathVariable UUID id, @RequestBody JournalEntry journalEntryToUpdate) {
        journalEntryService.updateJournalEntry(id, journalEntryToUpdate);
    }
    
}
