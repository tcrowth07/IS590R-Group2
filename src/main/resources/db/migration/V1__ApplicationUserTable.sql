CREATE TABLE ApplicationUser (
    id UUID NOT NULL PRIMARY KEY,
    Role VARCHAR(50),
    Name VARCHAR(100) NOT NULL,
    Username VARCHAR(100),
    Password Varchar(100),
    IsAccountNonExpired BOOLEAN,
    IsAccountNonLocked BOOLEAN,
    IsCredentialsNonExpired BOOLEAN,
    IsEnabled BOOLEAN
);

insert into ApplicationUser(id, role, name, username, password, IsAccountNonExpired, IsAccountNonLocked, IsCredentialsNonExpired, IsEnabled)
values('e9065b24-8b01-4d0c-81e3-fb794a83e952', 'ADMIN', 'Logan','testUN','$2a$10$/gZdxM3cyX6OdPzgWySzquPxYmbyJtE2ljp4tH/6QVB8YBqGynJdK', true, true, true, true)

