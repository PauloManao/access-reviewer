package project.webapp.accessreviewerapp.repositories;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import project.webapp.accessreviewerapp.entities.Review;


public interface ReviewRepository extends JpaRepository<Review, Long>{
	
	List<Review> findByAddressId(Long addressId);

    // New custom query method to select only the comments for a given address ID
    @Query("SELECT r.comments FROM Review r WHERE r.address.id = :addressId")
    List<String> findCommentsByAddressId(@Param("addressId") Long addressId);
    
    @Query("SELECT r FROM Review r WHERE r.address.id = :addressId AND r.isEnabled = true")
    List<Review> findEnabledByAddressId(@Param("addressId") Long addressId);
}
