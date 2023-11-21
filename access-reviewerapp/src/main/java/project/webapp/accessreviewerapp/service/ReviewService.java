package project.webapp.accessreviewerapp.service;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import project.webapp.accessreviewerapp.dto.ReviewDto;
import project.webapp.accessreviewerapp.dto.UserDto;
import project.webapp.accessreviewerapp.entities.Address;
import project.webapp.accessreviewerapp.entities.Image;
import project.webapp.accessreviewerapp.entities.Review;
import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.repositories.AddressRepository;
import project.webapp.accessreviewerapp.repositories.ImageRepository;
import project.webapp.accessreviewerapp.repositories.ReviewRepository;
import project.webapp.accessreviewerapp.repositories.UserRepository;
import project.webapp.accessreviewerapp.util.S3Util;

@Service
public class ReviewService {
	
    @Autowired
    private AddressRepository addressRepository;

    @Autowired
    private ReviewRepository reviewRepository;
    
    @Autowired
    private ImageRepository imageRepository;
    
    @Autowired
    private UserRepository userRepository; // Add UserRepository

    
    // Base URL for accessing the uploaded files (you can set this in your application.properties)
    @Value("${app.s3.bucket.url}")
    private String bucketBaseUrl;
    
	public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }

    public Review submitReview(String addressString, ReviewDto reviewDto, List<MultipartFile> imageFiles) {
    	
    	  // Fetch the user or throw exception if not found
        User user = userRepository.findById(reviewDto.getUserId())
                                  .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        
    	
        // Logic to parse the address and potentially find existing or create new
        Address address = addressRepository.findByAddress(addressString)
            .orElseGet(() -> {
                Address newAddress = new Address();
                newAddress.setAddress(addressString);
                // Any other necessary address fields set up here
                return addressRepository.save(newAddress);
            });
        
 
        // Now you have an Address object with an ID, whether it was new or existing
        Review review = new Review();
        
        review.setAddress(address);
        // Populate the fields from ReviewDto to Review
        review.setRateExperience(reviewDto.getRateExperience());
        review.setEntrance(reviewDto.getEntrance());
        review.setAccessToServices(reviewDto.getAccessToServices());
        review.setSeatsTablesCounters(reviewDto.getSeatsTablesCounters());
        review.setRestRooms(reviewDto.getRestRooms());
        review.setComments(reviewDto.getComments());
        review.setUser(user); // Set the user to the review
        review.setSubmissionDate(LocalDateTime.now()); // Set current date and time
     
        
        // Save the review first to generate the ID for the review
        review = reviewRepository.save(review);
        
        // Process image files if they exist
        if (imageFiles != null && !imageFiles.isEmpty()) {
            List<Image> images = new ArrayList<>();
            for (MultipartFile file : imageFiles) {
                String imageUrl = uploadImageToS3(file);
                if (imageUrl != null) { // Make sure the URL is not null
                    Image image = new Image();
                    image.setImageUrl(imageUrl);
                    image.setReview(review);
                    images.add(image);
                }
            }
            if (!images.isEmpty()) {
                imageRepository.saveAll(images); // Save all images to the database
                review.setImages(images); // Set the images to the review
            }
        }

        return review; // Return the updated review with images
        
    }
    
    private String uploadImageToS3(MultipartFile file) {
        if (!file.isEmpty()) {
            try {
                String fileName = file.getOriginalFilename();
                // Upload file to S3 and capture the response
                S3Util.uploadFile(fileName, file.getInputStream());
                // Construct the complete URL for the uploaded image
                return S3Util.getFileUrl(fileName, bucketBaseUrl);
            } catch (IOException e) {
                // Log and handle the exception according to your application's requirements
                e.printStackTrace();
            }
        }
        return null;
    }
    
    
    // Method to get all comments by address ID
    public List<String> getCommentsByAddressId(Long addressId) {
        List<Review> reviews = reviewRepository.findByAddressId(addressId);
        return reviews.stream()
                      .map(Review::getComments)
                      .collect(Collectors.toList());
    }
    
    
    //method  to update in the reviewer page
    public Review updateReview(Long id, ReviewDto reviewDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: " + id));

        // Update the review details
        review.setRateExperience(reviewDto.getRateExperience());
        review.setEntrance(reviewDto.getEntrance());
        review.setAccessToServices(reviewDto.getAccessToServices());
        review.setSeatsTablesCounters(reviewDto.getSeatsTablesCounters());
        review.setRestRooms(reviewDto.getRestRooms());
        review.setComments(reviewDto.getComments());

        return reviewRepository.save(review);
    }
    
    
    // method to delete review
    public void deleteReview(Long id) {
        reviewRepository.deleteById(id);
    }
    
    
    //method to list all reviews
    
    public List<ReviewDto> findAll() {
        return reviewRepository.findAll().stream().map(review -> {
            ReviewDto dto = new ReviewDto();
            dto.setId(review.getId());
            dto.setAccessToServices(review.getAccessToServices());
            dto.setComments(review.getComments());
            dto.setEntrance(review.getEntrance());
            dto.setRateExperience(review.getRateExperience());
            dto.setRestRooms(review.getRestRooms());
            dto.setSeatsTablesCounters(review.getSeatsTablesCounters());
            dto.setSubmissionDate(review.getSubmissionDate());

            // Populate userId and addressId
            if (review.getUser() != null) {
                dto.setUserId(review.getUser().getId());
            }
            if (review.getAddress() != null) {
                // Assuming getAddress() returns the Address entity and you want the ID
                dto.setAddressId(review.getAddress().getId());
            }

            return dto;
        }).collect(Collectors.toList());
    }
    


}
