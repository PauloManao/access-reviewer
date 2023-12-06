package project.webapp.accessreviewerapp.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class MailConfig {

    @Value("${MAIL_USERNAME}")
    private String emailUsername;

    // other fields and methods
}
