package project.webapp.accessreviewerapp.service;

import java.io.IOException;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Service;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Service
public class CustomSucessHandler implements AuthenticationSuccessHandler{

	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response,
			Authentication authentication) throws IOException, ServletException {
		// TODO Auto-generated method stub
		
		var authorities = authentication.getAuthorities();
		var roles = authorities.stream().map(r -> r.getAuthority()).findFirst();
		
		if (roles.orElse("").equals("admin")) {
			response.sendRedirect("/index");
		} else if (roles.orElse("").equals("user")) {
			response.sendRedirect("/user-page");
		} else if (roles.orElse("").equals("reviewer")) {
			response.sendRedirect("/index");
		} else {
			response.sendRedirect("/error");
		}
		
	}
}
