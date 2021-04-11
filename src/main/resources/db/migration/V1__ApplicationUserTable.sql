CREATE TABLE ApplicationUser (
    id UUID NOT NULL PRIMARY KEY,
    GrantedAuthorities INT,
    Name VARCHAR(100) NOT NULL,
    Username VARCHAR(100),
    Password Varchar(50),
    IsAccountNonExpired BOOLEAN,
    IsAccountNonLocked BOOLEAN,
    IsCredentialsNonExpired BOOLEAN,
    IsEnabled BOOLEAN
);

insert into ApplicationUser(id, GrantedAuthorities, name, username, password, IsAccountNonExpired, IsAccountNonLocked, IsCredentialsNonExpired, IsEnabled)
values('e9065b24-8b01-4d0c-81e3-fb794a83e952', 1, 'Logan','testUN','password', 1, 1, 1, 1)

