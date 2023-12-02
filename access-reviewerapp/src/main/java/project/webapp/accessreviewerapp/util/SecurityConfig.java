package project.webapp.accessreviewerapp.util;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.util.matcher.AntPathRequestMatcher;

import project.webapp.accessreviewerapp.service.CustomAuthenticationFailureHandler;
import project.webapp.accessreviewerapp.service.CustomSucessHandler;
import project.webapp.accessreviewerapp.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	CustomSucessHandler customSucessHandler;
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	@Autowired
	CustomAuthenticationFailureHandler customAuthenticationFailureHandler;
	
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http.csrf(c -> c.disable())
		.authorizeHttpRequests(request -> request.requestMatchers("/admin-page", "/admin", "admin_user_edit", 
				"admin_users", "/admin/users", "/admin/users/edit/*", "/admin/users/update/*", "/admin/users/delete/*")
				.hasAuthority("admin")
				.requestMatchers("/reviewer", "/reviews_list")
				.hasAuthority("reviewer")
				.requestMatchers("/user-page", "/user").hasAuthority("user")
				.requestMatchers("/registration", "/styles.css", "/script.js", "/index", "/login", "/logout", 
						"/image/**", "/news","/about","/details.html","/geocode","/save","/submit",
						"/reviews/comments/address/{addressId}","/comments","/review","/weather",
						"/api/isAuthenticated","/reportReview", "/").permitAll() //REGISTRATION
				.anyRequest().authenticated())
		
		.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
				.successHandler(customSucessHandler).permitAll())
		
        .formLogin(form -> form
                .loginPage("/login")
                .loginProcessingUrl("/login")
                .failureHandler(customAuthenticationFailureHandler) // Redirect to /login with error parameter
                .successHandler(customSucessHandler)
                .permitAll())
		
		.logout(form-> form.invalidateHttpSession(true).clearAuthentication(true)
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/registration?logout").permitAll());
		
		return http.build();
		
	}
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}
	
}
