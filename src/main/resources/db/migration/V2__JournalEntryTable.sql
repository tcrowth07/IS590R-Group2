CREATE TABLE journalentry (
    id UUID NOT NULL PRIMARY KEY,
    Title VARCHAR(200),
    Markdown VARCHAR,
    HTML VARCHAR,
    userid UUID,
    CONSTRAINT fk_user
       FOREIGN KEY(userid)
       REFERENCES ApplicationUser(id)
);