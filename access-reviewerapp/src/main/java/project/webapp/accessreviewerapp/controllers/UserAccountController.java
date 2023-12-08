package project.webapp.accessreviewerapp.controllers;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.service.EmailService;
import project.webapp.accessreviewerapp.service.PasswordValidator;
import project.webapp.accessreviewerapp.service.UserService;

@Controller
public class UserAccountController {

    @Autowired
    private UserService userService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/forgot-password")
    public String showForgotPasswordForm() {
        return "forgot_password";
    }
    
    // Forgot Password
    @PostMapping("/forgot-password")
    public String processForgotPassword(@RequestParam("email") String email, Model model) {
        User user = userService.findByEmail(email);
        if (user == null) {
            model.addAttribute("error", "No account found with that email.");
            return "forgot_password";
        }

        String token = userService.createPasswordResetToken(user);
        emailService.sendPasswordResetEmail(email, token);

        model.addAttribute("message", "A password reset link has been sent to " + email);
        return "forgot_password";
    }
    
    @GetMapping("/reset-password")
    public String showResetPasswordForm(@RequestParam("token") String token, Model model) {
        model.addAttribute("token", token);
        return "reset_password";
    }

    // Reset Password
    @PostMapping("/reset-password")
    public String processResetPassword(@RequestParam("token") String token,
            @RequestParam("newPassword") String newPassword, @RequestParam("confirmPassword") String confirmPassword,
            RedirectAttributes redirectAttributes,
            Model model) {
    	
        if (!newPassword.equals(confirmPassword)) {
            model.addAttribute("token", token); // Preserve the token
            model.addAttribute("passwordMismatch", true);
            return "reset_password";
        }

		List<String> passwordValidationErrors = PasswordValidator.validatePassword(newPassword);
		
		if (!passwordValidationErrors.isEmpty()) {
		model.addAttribute("token", token); // Preserve the token for the form
		model.addAttribute("passwordErrors", passwordValidationErrors);
		return "reset_password";
		}
		
		if (!userService.isResetTokenValid(token)) {
		redirectAttributes.addFlashAttribute("error", "Invalid or expired password reset token.");
		return "redirect:/reset-password";
		}
		
		userService.updatePassword(token, newPassword);
		redirectAttributes.addFlashAttribute("message", "Your password has been successfully reset.");
		return "redirect:/login";
	}
    
}