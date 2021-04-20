package com.example.demo.dao;

import com.example.demo.dao.ApplicationUserDao;
import com.example.demo.model.ApplicationUser;
//import com.example.demo.model.User;
import com.google.common.collect.Lists;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static com.example.demo.security.ApplicationUserRole.ADMIN;

@Repository("applicationUserPostgres")
public class ApplicationUserDataAccessService implements ApplicationUserDao {

    private final PasswordEncoder passwordEncoder;
    private final JdbcTemplate jdbcTemplate;
    private final NamedParameterJdbcTemplate namedParameterJdbcTemplate;

    @Autowired
    public ApplicationUserDataAccessService(PasswordEncoder passwordEncoder, JdbcTemplate jdbcTemplate, NamedParameterJdbcTemplate namedParameterJdbcTemplate){
        this.passwordEncoder = passwordEncoder;
        this.jdbcTemplate = jdbcTemplate;
        this.namedParameterJdbcTemplate = namedParameterJdbcTemplate;
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserByUsername(String username) {
        return getApplicationUsers()
                .stream()
                .filter(applicationUser -> username.equals(applicationUser.getUsername()))
                .findFirst();
    }

    //This one might be a duplicate of selectAllApplicationUsers
    private List<ApplicationUser> getApplicationUsers(){
        List<ApplicationUser> applicationUsers = Lists.newArrayList(
                new ApplicationUser(
                        UUID.randomUUID(),
                        "ADMIN",
                        //ADMIN.getGrantedAuthorities(),
                        "admin",
                        "admin",
                        passwordEncoder.encode("password"),
                        true,
                        true,
                        true,
                        true
                ));
        return applicationUsers;
    }

    //Everything below here originated in UserDataAccessService and was copied over and modified.
    @Override
    public ApplicationUser insertApplicationUser(UUID id, ApplicationUser user) {
        final String sql = "INSERT INTO ApplicationUser(id, grantedPermissions, name, username, password, isAccountNonExpired, isAccountNonLocked, isCredentialsNonExpired, isEnabled) values(:id, :grantedPermissions, :name, :username, :password, :isAccountNonExpired, :isAccountNonLocked, :isCredentialsNonExpired, :isEnabled)";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                .addValue("grantedPermissions", user.getAuthorities())
                .addValue("name", user.getName())
                .addValue("username", user.getUsername())
                .addValue("password", passwordEncoder.encode(user.getPassword()))
                .addValue("isAccountNonExpired", true)
                .addValue("isAccountNonLocked:", true)
                .addValue("isCredentialsNonExpired", true)
                .addValue("isEnabled", true);
        //.addValue("address", user.getPassword());
        namedParameterJdbcTemplate.update(sql, parameters);
//        return "User " + user.getName() + " was added.";
        return new ApplicationUser(id, user.getRole(), user.getName(), user.getUsername(), "*******", user.isAccountNonExpired(), user.isAccountNonLocked(), user.isCredentialsNonExpired(), user.isEnabled());
    }

    @Override
    public List<ApplicationUser> selectAllApplicationUsers() {
        final String sql = "SELECT * FROM ApplicationUser";
        return jdbcTemplate.query(sql, (resultSet, i) -> {
            UUID id = UUID.fromString(resultSet.getString("id"));
            String name = resultSet.getString("name");
            String role = resultSet.getString("role");
            //Set<? extends GrantedAuthority> grantedAuthorities = resultSet("grantedAuthorities");
            String username = resultSet.getString("username"); //?
            String password = "**********";
            Boolean accNonExp = resultSet.getBoolean("isAccountNonExpired");
            Boolean accNonLock = resultSet.getBoolean("isAccountNonLocked");
            Boolean credNonExp = resultSet.getBoolean("isCredentialsNonExpired");
            Boolean isEnabled = resultSet.getBoolean("isEnabled");
            return new ApplicationUser(id, role, name, username, password, accNonExp, accNonLock, credNonExp, isEnabled);
        });
    }

    @Override
    public Optional<ApplicationUser> selectApplicationUserById(UUID id) {
        final String sql = "SELECT * FROM ApplicationUser WHERE id = ?";
        ApplicationUser user = jdbcTemplate.queryForObject(
                sql,
                new Object[]{id},
                (resultSet, i) -> {
                    UUID userId = UUID.fromString(resultSet.getString("id"));
                    String name = resultSet.getString("name");
                    //Set<? extends GrantedAuthority> grantedAuthorities = resultSet("grantedAuthorities");
                    String role = resultSet.getString("role");
                    String username = resultSet.getString("username"); //?
                    String password = "**********";
                    Boolean accNonExp = resultSet.getBoolean("isAccountNonExpired");
                    Boolean accNonLock = resultSet.getBoolean("isAccountNonLocked");
                    Boolean credNonExp = resultSet.getBoolean("isCredentialsNonExpired");
                    Boolean isEnabled = resultSet.getBoolean("isEnabled");
                    return new ApplicationUser(userId, role, name, username, password, accNonExp, accNonLock, credNonExp, isEnabled);
                });
        return Optional.ofNullable(user);
    }

    @Override
    public String deleteApplicationUserById(UUID id) {
        final String sql = "DELETE FROM ApplicationUser WHERE id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id);
        namedParameterJdbcTemplate.update(sql, parameters);
        return "User" + id.toString() + "was added.";
    }

    @Override
    public ApplicationUser updateApplicationUserById(UUID id, ApplicationUser user) {
        final String sql = "UPDATE ApplicationUser SET  role = :role, name = :name, username = :username, password = :password, isAccountNonExpired = :isAccountNonExpired, isAccountNonLocked = :isAccountNonLocked, isCredentialsNonExpired = :isCredentialsNonExpired, isEnabled = :isEnabled where id = :id";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("id", id)
                //.addValue("grantedAuthorities", user.getAuthorities())
                .addValue("role", user.getRole())
                .addValue("name", user.getName())
                .addValue("username", user.getUsername())
                .addValue("password", user.getPassword())
                .addValue("isAccountNonExpired", user.isAccountNonExpired())
                .addValue("isAccountNonLocked", user.isAccountNonLocked())
                .addValue("isCredentialsNonExpired", user.isCredentialsNonExpired())
                .addValue("isEnabled", user.isEnabled());

        namedParameterJdbcTemplate.update(sql, parameters);
        return user;
    }

    @Override
    public Boolean login(String username, String password) {
        final String sql = "Select * from ApplicationUser where username = :username AND password = :password";
        SqlParameterSource parameters = new MapSqlParameterSource()
                .addValue("username", username)
                .addValue("password", passwordEncoder.encode(password));
//        namedParameterJdbcTemplate.query(sql, parameters);
        return true;


//        ApplicationUser user = jdbcTemplate.queryForObject(
//                sql,
//                new Object[]{id},
//                (resultSet, i) -> {
//                    UUID userId = UUID.fromString(resultSet.getString("id"));
//                    String name = resultSet.getString("name");
//                    //Set<? extends GrantedAuthority> grantedAuthorities = resultSet("grantedAuthorities");
//                    String role = resultSet.getString("role");
//                    String username = resultSet.getString("username"); //?
//                    String password = "**********";
//                    Boolean accNonExp = resultSet.getBoolean("isAccountNonExpired");
//                    Boolean accNonLock = resultSet.getBoolean("isAccountNonLocked");
//                    Boolean credNonExp = resultSet.getBoolean("isCredentialsNonExpired");
//                    Boolean isEnabled = resultSet.getBoolean("isEnabled");
//                    return new ApplicationUser(userId, role, name, username, password, accNonExp, accNonLock, credNonExp, isEnabled);
//                });
    }

//    insert into ApplicationUser(id, name, username, password, IsAccountNonExpired, IsAccountNonLocked, IsCredentialsNonExpired, IsEnabled)
//    values('e9065b24-8b01-4d0c-81e3-fb794a83e952', 'Logan','testUN','password', 1, 1, 1, 1)

}
