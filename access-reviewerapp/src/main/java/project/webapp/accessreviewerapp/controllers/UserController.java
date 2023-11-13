package project.webapp.accessreviewerapp.controllers;

import java.security.Principal;
import java.util.HashMap;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import jakarta.servlet.http.HttpSession;
import project.webapp.accessreviewerapp.dto.UserDto;
import project.webapp.accessreviewerapp.service.UserService;

@Controller
public class UserController {
	
	@Autowired
	UserDetailsService userDetailsService;
	
	
	@Autowired
	private UserService userService;
	
	
	@GetMapping("/registration")
	public String getRegistrationPage(@ModelAttribute("user") UserDto userDto, Model model) {
		return "login";
	}
	
	
	@PostMapping("/registration")
	public String saveUser(@ModelAttribute("user") UserDto userDto, Model model) {
		
		// Set the user's role to "user" before saving
		userDto.setRole("user");
		userService.save(userDto);
		model.addAttribute("message", "Registered Successfuly");
		
		return "login";
	}
	
	
	@GetMapping("/login")
	public String login() {
		return "login";
	}
		
	@GetMapping("user-page")
	public String userPage (Model model, Principal principal) {
		UserDetails userDetails = userDetailsService.loadUserByUsername(principal.getName());
		model.addAttribute("user", userDetails);
		return "index";
	}
	
	@GetMapping("admin-page")
	public String adminPage (Model model, Principal principal) {
		return "index";
	}
	
	//check the login status
    @GetMapping("/api/isAuthenticated")
    public ResponseEntity<Boolean> isAuthenticated(Principal principal) {
        return new ResponseEntity<>(principal != null, HttpStatus.OK);
    }
    
    

    
	
}
