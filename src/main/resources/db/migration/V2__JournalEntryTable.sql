CREATE TABLE journalentry (
    id UUID NOT NULL PRIMARY KEY,
    Title VARCHAR(200),
    Markdown VARCHAR,
    HTML VARCHAR
);