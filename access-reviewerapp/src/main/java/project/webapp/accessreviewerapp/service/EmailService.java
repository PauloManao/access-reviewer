package project.webapp.accessreviewerapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
public class EmailService {

    @Autowired
    private JavaMailSender mailSender;

    public void sendPasswordResetEmail(String to, String token) {
        String subject = "Password Reset Request";
        //String resetUrl = "http://localhost:8080/reset-password?token=" + token;
        String resetUrl = "https://accessreviewer-c3dba0728822.herokuapp.com/reset-password?token=" + token;
        String text = "To reset your password, click the link below:\n" + resetUrl;

        SimpleMailMessage email = new SimpleMailMessage();
        email.setFrom("accessibilityapp@outlook.com");
        email.setTo(to);
        email.setSubject(subject);
        email.setText(text);
        mailSender.send(email);
    }
    
}
