package project.webapp.accessreviewerapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import project.webapp.accessreviewerapp.entities.PasswordResetToken;
import project.webapp.accessreviewerapp.entities.User;

@Repository
public interface PasswordResetTokenRepository extends JpaRepository<PasswordResetToken, Long> {
    PasswordResetToken findByToken(String token);
    
    PasswordResetToken findByUser(User user);
}
