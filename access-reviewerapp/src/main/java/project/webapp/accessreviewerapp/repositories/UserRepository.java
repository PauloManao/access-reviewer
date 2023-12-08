package project.webapp.accessreviewerapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import project.webapp.accessreviewerapp.entities.User;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	
	User findByEmail (String email);
	
    // Search method
    @Query("SELECT u FROM User u WHERE LOWER(u.username) LIKE LOWER(CONCAT('%', :searchTerm, '%')) OR LOWER(u.email) LIKE LOWER(CONCAT('%', :searchTerm, '%'))")
    List<User> findByUsernameOrEmailContaining(@Param("searchTerm") String searchTerm);
	
}
