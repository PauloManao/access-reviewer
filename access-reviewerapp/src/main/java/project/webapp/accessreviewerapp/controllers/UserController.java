package project.webapp.accessreviewerapp.controllers;

import java.security.Principal;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import project.webapp.accessreviewerapp.dto.UserDto;
import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.service.UserService;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import java.util.Collection;
import java.util.List;

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
		
		//check if the email address is already in use
        if (userService.emailExists(userDto.getEmail())) {
            model.addAttribute("message", "Email address already in use");
            model.addAttribute("stayOnSignup", true); 
            return "login"; // Assuming 'login' is the registration page
        }
		// Set the user's role to "user" before saving
		userDto.setRole("user");
		userService.save(userDto);
		model.addAttribute("message", "Registered Successfully");
		
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
    
	//redirect the users to their personal page according to their roles when they click on their name 
    @GetMapping("/account")
    public String accountRedirect(Principal principal) {
        if (principal instanceof Authentication) {
            Authentication authentication = (Authentication) principal;
            Collection<? extends GrantedAuthority> authorities = authentication.getAuthorities();

            boolean isAdmin = authorities.stream().anyMatch(a -> a.getAuthority().equals("admin"));
            boolean isReviewer = authorities.stream().anyMatch(a -> a.getAuthority().equals("reviewer"));

            if (isAdmin) {
                return "redirect:/admin";
            } else if (isReviewer) {
                return "redirect:/reviewer"; // Redirect to the reviewer page
            } else {
                return "redirect:/user"; // Default to user page for other roles
            }
        }

        return "redirect:/login"; // Redirect to login if no principal or not authenticated
    }
	
	@GetMapping("admin")
	public String admin (Model model, Principal principal) {
		return "admin";
	}
	
	@GetMapping("user")
	public String user (Model model, Principal principal) {
		return "user";
	}
	
	@GetMapping("reviewer")
	public String reviewer (Model model, Principal principal) {
		return "reviewer";
	}
	
	
	@GetMapping("/admin/users")
	public String listUsers(Model model) {
	    List<UserDto> users = userService.findAll();
	    model.addAttribute("users", users);
	    return "admin_users";
	}

	@PostMapping("/admin/users")
	public String addUser(@ModelAttribute("user") UserDto userDto, RedirectAttributes redirectAttributes) {
	    User savedUser = userService.save(userDto);
	    redirectAttributes.addFlashAttribute("adminMessage", "New user added successfully with ID: " + savedUser.getId());
	    return "redirect:/admin/users";
	}

	@GetMapping("/admin/users/edit/{id}")
	public String editUserForm(@PathVariable Long id, Model model) {
	    UserDto userDto = userService.findById(id);
	    model.addAttribute("user", userDto);	    
	    return "admin_user_edit";
	}

	@PostMapping("/admin/users/update/{id}")
	public String updateUser(@ModelAttribute("user") UserDto userDto, @PathVariable Long id, RedirectAttributes redirectAttributes) {
	    userService.update(userDto, id);
	    redirectAttributes.addFlashAttribute("adminMessage", "User updated successfully with ID: " + id);
	    return "redirect:/admin/users";
	}

	@GetMapping("/admin/users/delete/{id}")
	public String deleteUser(@PathVariable Long id, RedirectAttributes redirectAttributes) {
	    userService.delete(id);
	    redirectAttributes.addFlashAttribute("adminMessage", "User deleted successfully with ID: " + id);
	    return "redirect:/admin/users";
	}

       
	
}
