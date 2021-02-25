package com.example.demo.model;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public class JournalEntry {

    private final UUID id;
    private final String title;
    private final String markdown;
    private final String html;
    private final UUID userid;

    public JournalEntry(@JsonProperty("id") UUID id,
                @JsonProperty("title") String title,
                @JsonProperty("markdown") String markdown,
                @JsonProperty("html") String html,
                @JsonProperty("userid") UUID userid
    ) {
        this.id = id;
        this.title = title;
        this.markdown = markdown;
        this.html = html;
        this.userid = userid;
    }

    public UUID getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getMarkdown() {return markdown;}

    public String getHtml() {return html;}

    public UUID getUserid() {return userid;}
}
