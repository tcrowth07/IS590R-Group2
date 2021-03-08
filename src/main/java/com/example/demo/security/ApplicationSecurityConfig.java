package com.example.demo.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;
import org.springframework.security.web.csrf.CookieCsrfTokenRepository;

import static com.example.demo.security.ApplicationUserRole.ADMIN;
import static com.example.demo.security.ApplicationUserRole.USER;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)

public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter{

    private final PasswordEncoder passwordEncoder;

    @Autowired
    public ApplicationSecurityConfig(PasswordEncoder passwordEncoder) {
        this.passwordEncoder = passwordEncoder;
    }

    @Override
    protected  void configure(HttpSecurity http) throws Exception{
        http
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .and()
                .csrf().disable()
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers("/api/**").hasRole(USER.name())
//                .antMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority(ApplicationUserPermission.JOURNAL_WRITE.name())
//                .antMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority(ApplicationUserPermission.JOURNAL_WRITE.name())
//                .antMatchers(HttpMethod.PUT, "/api/**").hasAuthority(ApplicationUserPermission.JOURNAL_WRITE.name())
//                .antMatchers(HttpMethod.GET, "/api/**").hasAuthority(ApplicationUserPermission.JOURNAL_READ.name())
                .anyRequest()
                .authenticated()
                .and()
                .formLogin()
                .loginPage("/login").permitAll();
    }

    @Override
    @Bean
    protected UserDetailsService userDetailsService(){
        UserDetails testUser = User.builder()
                .username("test")
                .password(passwordEncoder.encode("password"))
                .authorities(USER.getGrantedAuthorities())
                //.roles(ApplicationUserRole.USER.name())
                .build();

        UserDetails adminUser = User.builder()
                .username("admin")
                .password(passwordEncoder.encode("password"))
                //.roles(ApplicationUserRole.ADMIN.name())
                .authorities(ADMIN.getGrantedAuthorities())
                .build();

        return new InMemoryUserDetailsManager(
                testUser,
                adminUser
        );
    }
}



