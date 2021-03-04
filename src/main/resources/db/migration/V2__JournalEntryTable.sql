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

insert into journalentry(id, Title, Markdown, HTML, userID)
values('5ac8209e-4989-42a9-9cbf-9f185d5c366e','First Journal Entry','*Hello World','<h1>Hello World</h1>','e9065b24-8b01-4d0c-81e3-fb794a83e952')
