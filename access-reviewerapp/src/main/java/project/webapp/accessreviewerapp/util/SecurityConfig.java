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

import project.webapp.accessreviewerapp.service.CustomSucessHandler;
import project.webapp.accessreviewerapp.service.CustomUserDetailsService;

@Configuration
@EnableWebSecurity
public class SecurityConfig {
	
	@Autowired
	CustomSucessHandler customSucessHandler;
	
	@Autowired
	CustomUserDetailsService customUserDetailsService;
	
	@Bean
	public static PasswordEncoder passwordEncoder() {
		return new BCryptPasswordEncoder();
	}
	
	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception{
		
		http.csrf(c -> c.disable())
		.authorizeHttpRequests(request -> request.requestMatchers("/admin-page")
				.hasAuthority("admin").requestMatchers("/user-page").hasAuthority("user")
				.requestMatchers("/registration", "/styles.css", "/script.js", "/index", "/login", "/logout", 
						"/image/**", "/news","/about","/details.html","/geocode","/save","/submit",
						"/reviews/comments/address/{addressId}","/comments","/review","/weather",
						"/api/isAuthenticated","/").permitAll() //REGISTRATION
				.anyRequest().authenticated())
		
		.formLogin(form -> form.loginPage("/login").loginProcessingUrl("/login")
				.successHandler(customSucessHandler).permitAll())
		
		.logout(form-> form.invalidateHttpSession(true).clearAuthentication(true)
				.logoutRequestMatcher(new AntPathRequestMatcher("/logout"))
				.logoutSuccessUrl("/login?logout").permitAll());
		
		return http.build();
		
	}
	
	@Autowired
	public void configure(AuthenticationManagerBuilder auth) throws Exception{
		auth.userDetailsService(customUserDetailsService).passwordEncoder(passwordEncoder());
	}
	
}
