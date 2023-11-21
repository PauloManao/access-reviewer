package project.webapp.accessreviewerapp.controllers;


import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;


@Controller
public class ThymeleafReviewController {
	
	
    @RequestMapping("/review")
    public String showReviewPage() {
        return "review"; 
    }

}

