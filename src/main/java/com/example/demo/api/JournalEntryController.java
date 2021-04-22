package com.example.demo.api;

import com.example.demo.model.JournalEntry;
import com.example.demo.service.JournalEntryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@CrossOrigin(origins = "http://localhost:3000")
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
//    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public void addJournalEntry(@RequestBody JournalEntry journalEntry) {
        journalEntryService.addJournalEntry(journalEntry);
    }

    @GetMapping
//    @PreAuthorize("hasAnyRole('ROLE_ADMIN')")
    public List<JournalEntry> getAllJournalEntries() {
        return journalEntryService.getAllJournalEntries();
    }

    @GetMapping(path = "/user/{userid}")
//    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public List<JournalEntry> getAllJournalEntriesByUserId(@PathVariable("userid") UUID userid){
        return journalEntryService.getAllJournalEntriesByUserId(userid);
    }

    @GetMapping(path = "/{id}")
//    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public JournalEntry getJournalEntryById(@PathVariable("id") UUID id){
        return journalEntryService.getJournalEntryById(id).orElse(null);
    }

    @DeleteMapping(path = "{id}")
//    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public void deleteJournalEntryById(@PathVariable UUID id){
        journalEntryService.deleteJournalEntry(id);
    }

    @PutMapping(path = "{id}")
//    @PreAuthorize("hasAnyRole('ROLE_USER')")
    public void updateJournalEntry(@PathVariable UUID id, @RequestBody JournalEntry journalEntryToUpdate) {
        journalEntryService.updateJournalEntry(id, journalEntryToUpdate);
    }
    
}
