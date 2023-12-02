package project.webapp.accessreviewerapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;


import project.webapp.accessreviewerapp.service.UserServiceImpl;

@Controller
public class LoginController {
	
	@Autowired
	private UserServiceImpl userServiceImpl;
	
    @RequestMapping("/login")
    public String login(Model model) {
        if (!model.containsAttribute("user")) {
            model.addAttribute("user", userServiceImpl.getEmptyUserDto());
        }
        return "login";
    }
    
    
}
