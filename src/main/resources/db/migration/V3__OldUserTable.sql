CREATE TABLE ApplicationUser (
    id UUID NOT NULL PRIMARY KEY,
    Name VARCHAR(100) NOT NULL,
    Email VARCHAR(100),
    Password Varchar(50)
);

insert into ApplicationUser(id, name, email, password)
values('e9065b24-8b01-4d0c-81e3-fb794a83e952', 'Logan','test@test.com','password')

