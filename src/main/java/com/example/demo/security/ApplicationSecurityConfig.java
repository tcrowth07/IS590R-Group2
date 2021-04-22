package com.example.demo.security;

import com.example.demo.dao.ApplicationUserDao;
import com.example.demo.service.ApplicationUserService;
import com.example.demo.jwt.JwtConfig;
import com.example.demo.jwt.JwtTokenVerifier;
import com.example.demo.jwt.JwtUsernameAndPasswordAuthenticationFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.method.configuration.EnableGlobalMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configuration.WebSecurityConfigurerAdapter;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.session.SessionManagementFilter;
import org.springframework.web.cors.CorsConfiguration;

import javax.crypto.SecretKey;
import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import java.io.IOException;

import static com.example.demo.security.ApplicationUserRole.USER;

@Configuration
@EnableWebSecurity
@EnableGlobalMethodSecurity(prePostEnabled = true)
public class ApplicationSecurityConfig extends WebSecurityConfigurerAdapter{

    private final PasswordEncoder passwordEncoder;
    private final ApplicationUserService applicationUserService;
    private final SecretKey secretKey;
    private final JwtConfig jwtConfig;
    private final ApplicationUserDao applicationUserDao;

    @Autowired
    public ApplicationSecurityConfig(
            PasswordEncoder passwordEncoder,
            ApplicationUserService applicationUserService,
            SecretKey secretKey,
            JwtConfig jwtConfig,
            ApplicationUserDao applicationUserDao) {
        this.passwordEncoder = passwordEncoder;
        this.applicationUserService = applicationUserService;
        this.secretKey = secretKey;
        this.jwtConfig = jwtConfig;
        this.applicationUserDao = applicationUserDao;
    }

    @Bean
    CorsFilter corsFilter() {
        CorsFilter filter = new CorsFilter();
        return filter;
    }

    @Override
    protected  void configure(HttpSecurity http) throws Exception{
        var jwtTokenFilter = new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), this.jwtConfig, this.secretKey);
        var jwtTokenVerifier = new JwtTokenVerifier(this.secretKey, this.jwtConfig, this.applicationUserDao); //does the userDao need to be here?
//                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
//                .and()
        http
                //.cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .cors().configurationSource(request -> new CorsConfiguration().applyPermitDefaultValues())
                .and()
                //.addFilterBefore(corsFilter(), SessionManagementFilter.class) //adds your custom CorsFilter
                .csrf().disable()
                .sessionManagement()
                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                .and()
                .addFilter(jwtTokenFilter)
                .addFilterAfter(jwtTokenVerifier, JwtUsernameAndPasswordAuthenticationFilter.class)
                .authorizeRequests()
                .antMatchers("/", "index", "/css/*", "/js/*").permitAll()
                .antMatchers(HttpMethod.POST,"/api/v1/user").permitAll()
                .anyRequest()
                .authenticated();

    }



//    @Override
//    protected  void configure(HttpSecurity http) throws Exception{
//        var jwtTokenFilter = new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), this.jwtConfig, this.secretKey);
//        var jwtTokenVerifier = new JwtTokenVerifier(this.secretKey, this.jwtConfig, this.applicationUserDao); //does the userDao need to be here?
////                .csrf().csrfTokenRepository(CookieCsrfTokenRepository.withHttpOnlyFalse())
////                .and()
//        http
//                .csrf().disable()
//                .sessionManagement()
//                .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
//                .and()
//                .addFilter(jwtTokenFilter)
//                //.addFilter(new JwtUsernameAndPasswordAuthenticationFilter(authenticationManager(), jwtConfig, secretKey))
//                //.addFilterAfter(new JwtTokenVerifier(secretKey, jwtConfig), JwtUsernameAndPasswordAuthenticationFilter.class)
//                .addFilterAfter(jwtTokenVerifier, JwtUsernameAndPasswordAuthenticationFilter.class)
//                .authorizeRequests()
//                //.antMatchers("/", "index", "/css/*", "/js/*").permitAll()
//                .antMatchers("/api/v1/user").permitAll()
//                .antMatchers("/login*").permitAll()
//                .antMatchers("/login").permitAll()
                //.antMatchers("/api/**").hasRole(USER.name())
//                .antMatchers(HttpMethod.DELETE, "/api/**").hasAnyAuthority(ApplicationUserPermission.JOURNAL_WRITE.name())
//                .antMatchers(HttpMethod.POST, "/api/**").hasAnyAuthority(ApplicationUserPermission.JOURNAL_WRITE.name())
//                .antMatchers(HttpMethod.PUT, "/api/**").hasAuthority(ApplicationUserPermission.JOURNAL_WRITE.name())
//                .antMatchers(HttpMethod.GET, "/api/**").hasAuthority(ApplicationUserPermission.JOURNAL_READ.name())
//                .anyRequest()
//                .authenticated()
//                .and()
//                .formLogin()
//                .loginPage("/login.html")
//                .loginProcessingUrl("/perform_login")
//                .defaultSuccessUrl("/homepage.html", true)
//                .failureUrl("/login.html?error=true")
//                .failureHandler(authenticationFailureHandler())
//                .and()
//                .logout()
//                .logoutUrl("/perform_logout")
//                .deleteCookies("JSESSIONID")
//                .logoutSuccessHandler(logoutSuccessHandler());


        ;

//    }


    @Override
    protected void configure(AuthenticationManagerBuilder auth) throws Exception{
        auth.authenticationProvider(daoAuthenticationProvider());
//        auth.inMemoryAuthentication()
//                .withUser("user1").password(passwordEncoder.encode("user1Pass")).roles("USER")
//                .and()
//                .withUser("user2").password(passwordEncoder.encode("user2Pass")).roles("USER")
//                .and()
//                .withUser("admin").password(passwordEncoder.encode("adminPass")).roles("ADMIN");
    }

    @Bean
    public DaoAuthenticationProvider daoAuthenticationProvider(){
        DaoAuthenticationProvider provider = new DaoAuthenticationProvider();
        provider.setPasswordEncoder(passwordEncoder);
        provider.setUserDetailsService(applicationUserService);
        return provider;
    }


}


class CorsFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) throws ServletException {

    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletResponse response = (HttpServletResponse) servletResponse;
        HttpServletRequest request= (HttpServletRequest) servletRequest;

        response.setHeader("Access-Control-Allow-Origin", "*");
        response.setHeader("Access-Control-Allow-Methods", "GET,POST,DELETE,PUT,OPTIONS");
        response.setHeader("Access-Control-Allow-Headers", "*");
//        response.setHeader("Access-Control-Allow-Credentials", true);
//        response.setHeader("Access-Control-Max-Age", 180);
        filterChain.doFilter(servletRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}


//    @Override
//    @Bean
//    protected UserDetailsService userDetailsService(){
//        UserDetails testUser = User.builder()
//                .username("test")
//                .password(passwordEncoder.encode("password"))
//                .authorities(USER.getGrantedAuthorities())
//                //.roles(ApplicationUserRole.USER.name())
//                .build();
//
//        UserDetails adminUser = User.builder()
//                .username("admin")
//                .password(passwordEncoder.encode("password"))
//                //.roles(ApplicationUserRole.ADMIN.name())
//                .authorities(ADMIN.getGrantedAuthorities())
//                .build();
//
//        return new InMemoryUserDetailsManager(
//                testUser,
//                adminUser
//        );
//    }

//.and()
//        .formLogin()
//        .loginPage("/login").permitAll()
//        .defaultSuccessUrl("/journals", true)
//        .passwordParameter("password")
//        .usernameParameter("username")
//        .and()
//        .rememberMe().tokenValiditySeconds((int) TimeUnit.DAYS.toSeconds(21))
//        .rememberMeParameter("remember-me")
//        .key("somethingverysecured")
//        .and()
//        .logout()
//        .logoutUrl("/logout")
//        .logoutRequestMatcher(new AntPathRequestMatcher("/logout", "GET"))
//        .clearAuthentication(true)
//        .invalidateHttpSession(true)
//        .deleteCookies("JSESSIONID", "remember-me")
//        .logoutSuccessUrl("/login");

