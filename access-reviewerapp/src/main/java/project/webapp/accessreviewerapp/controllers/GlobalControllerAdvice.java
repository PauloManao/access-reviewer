package project.webapp.accessreviewerapp.controllers;

import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import project.webapp.accessreviewerapp.service.CustomUserDetail;

@ControllerAdvice
public class GlobalControllerAdvice {
	
    @ModelAttribute("username")
    public String currentUserName(Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() 
                && !"anonymousUser".equals(authentication.getName())) {
            Object principal = authentication.getPrincipal();
            if (principal instanceof CustomUserDetail) {
                return ((CustomUserDetail) principal).getName(); // Make sure this method exists in CustomUserDetail
            }
        }
        return "";
    }

}
