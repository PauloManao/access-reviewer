package project.webapp.accessreviewerapp.service;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.OptionalDouble;
import java.util.stream.Collectors;
import java.util.stream.DoubleStream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import project.webapp.accessreviewerapp.dto.AddressRatingDto;
import project.webapp.accessreviewerapp.dto.ReviewDto;
import project.webapp.accessreviewerapp.entities.Address;
import project.webapp.accessreviewerapp.entities.Image;
import project.webapp.accessreviewerapp.entities.Review;
import project.webapp.accessreviewerapp.entities.ReviewReport;
import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.repositories.AddressRepository;
import project.webapp.accessreviewerapp.repositories.ImageRepository;
import project.webapp.accessreviewerapp.repositories.ReviewReportRepository;
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
    private UserRepository userRepository; 
    
    @Autowired
    private ReviewReportRepository reviewReportRepository;

    
    // Base URL for accessing the uploaded files (you can set this in your application.properties)
    @Value("${app.s3.bucket.url}")
    private String bucketBaseUrl;
    
	public Review saveReview(Review review) {
        return reviewRepository.save(review);
    }
	
	//report review functionality
	
    public void reportReview(Long reviewId, String reportReason) {
        Review review = reviewRepository.findById(reviewId)
                .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        ReviewReport report = new ReviewReport();
        report.setReview(review);
        report.setReportReason(reportReason);
        report.setReportedAt(LocalDateTime.now());

        reviewReportRepository.save(report);
    }
	
	
	//submit review
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
    public List<ReviewDto> getCommentsByAddressId(Long addressId) {
        List<Review> reviews = reviewRepository.findByAddressId(addressId);
        return reviews.stream()
                      .map(review -> {
                          ReviewDto dto = new ReviewDto();
                          dto.setComments(review.getComments());
                          dto.setUsername(review.getUser().getUsername()); // Set the username
                          dto.setId(review.getId());
                          // Set other fields if necessary
                          return dto;
                      })
                      .collect(Collectors.toList());
    }
    
    
    //method  to update in the reviewer page
    public Review updateReview(Long id, ReviewDto reviewDto) {
        Review review = reviewRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Review not found with ID: "));

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

            // Populate username and address
            if (review.getUser() != null) {
                dto.setUsername(review.getUser().getUsername()); //dto.setUserId(review.getUser().getId());
            }
            if (review.getAddress() != null) {
                // Assuming getAddress() returns the Address entity and you want the ID
                dto.setAddressString(review.getAddress().getAddress());
            }
            
            int reportCount = reviewReportRepository.countByReviewId(review.getId());
            dto.setReportCount(reportCount);
            
            dto.setEnabled(review.isEnabled());

            return dto;
        }).collect(Collectors.toList());
    }
    
    
    public ReviewDto findById(Long id) {
        Review review = reviewRepository.findById(id).orElseThrow(() -> new RuntimeException("Review not found"));
        ReviewDto reviewDto = new ReviewDto(review.getAccessToServices(), 
        		review.getSeatsTablesCounters(), 
        		review.getEntrance(),
        		review.getRateExperience(),
        		review.getRestRooms(),review.getComments());
        reviewDto.setId(review.getId()); // Make sure this line is setting the ID
        reviewDto.setEnabled(review.isEnabled());
        
        // Set address details
        if (review.getAddress() != null) {
            reviewDto.setAddressString(review.getAddress().getAddress());
        }
        
        if (review.getUser() != null) {
            reviewDto.setUsername(review.getUser().getUsername());
        }
        
        // Get and set the report count for this review
        int reportCount = reviewReportRepository.countByReviewId(id);
        reviewDto.setReportCount(reportCount);
        
        return reviewDto;
    }
    
    public void toggleReviewEnabled(Long id) {
        Review review = reviewRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Review not found"));
        review.toggleEnabled();
        reviewRepository.save(review);
    }
    
    //search review by address
    public List<ReviewDto> searchReviewsByAddress(String searchTerm) {
        return reviewRepository.findByAddressStringContaining(searchTerm).stream()
            .map(review -> convertToDto(review))
            .collect(Collectors.toList());
    }

    private ReviewDto convertToDto(Review review) {
        ReviewDto dto = new ReviewDto();
        dto.setId(review.getId());
        if (review.getAddress() != null) {
            dto.setAddressString(review.getAddress().getAddress());
        }
        dto.setRateExperience(review.getRateExperience());
        dto.setEntrance(review.getEntrance());
        dto.setAccessToServices(review.getAccessToServices());
        dto.setSeatsTablesCounters(review.getSeatsTablesCounters());
        dto.setRestRooms(review.getRestRooms());
        dto.setComments(review.getComments());
        dto.setSubmissionDate(review.getSubmissionDate());
        dto.setEnabled(review.isEnabled());
        if (review.getUser() != null) {
            dto.setUsername(review.getUser().getUsername());
        }
        
        return dto;
    }
    

    public List<ReviewDto> getEnabledCommentsByAddressId(Long addressId) {
        List<Review> enabledReviews = reviewRepository.findEnabledByAddressId(addressId);
        return enabledReviews.stream()
                             .map(review -> findById(review.getId())) // Use findById for conversion
                             .collect(Collectors.toList());
    }
    
    
    public List<String> getImageUrlsForAddress(String addressString) {
        Address address = addressRepository.findByAddress(addressString)
                            .orElseThrow(() -> new EntityNotFoundException("Address not found"));
        
        List<Review> reviews = reviewRepository.findByAddressId(address.getId());
        return reviews.stream()
                      .flatMap(review -> review.getImages().stream())
                      .map(Image::getImageUrl)
                      .collect(Collectors.toList());
    }
    

    public List<ReviewDto> findReviewsByUserId(Long userId) {
        return reviewRepository.findByUserId(userId).stream()
                .map(review -> {
                    // Create a new ReviewDto object
                    ReviewDto dto = new ReviewDto();

                    // Set only the necessary fields: submission date, address, and comments
                    dto.setSubmissionDate(review.getSubmissionDate());
                    dto.setAddressString(review.getAddress() != null ? review.getAddress().getAddress() : null); // assuming getAddress() returns the address string
                    dto.setComments(review.getComments());
                    dto.setId(review.getId()); // You might need the review ID for reference

                    // Return the dto object
                    return dto;
                })
                .collect(Collectors.toList());
    }
    
    //method for calculating average rates
    public List<AddressRatingDto> getAddressRatings() {
        List<Review> allReviews = reviewRepository.findAll();
        
        return allReviews.stream()
            .collect(Collectors.groupingBy(review -> review.getAddress().getAddress()))
            .entrySet().stream()
            .map(entry -> {
                String address = entry.getKey();
                List<Review> reviews = entry.getValue();

                double overallRate = calculateAverage(reviews.stream()
                    .mapToDouble(review -> (review.getRateExperience() 
                                            + review.getEntrance() 
                                            + review.getAccessToServices() 
                                            + review.getSeatsTablesCounters() 
                                            + review.getRestRooms()) / 5.0));

                double averageRateExperience = calculateAverage(reviews.stream().mapToDouble(Review::getRateExperience));
                double averageEntrance = calculateAverage(reviews.stream().mapToDouble(Review::getEntrance));
                double averageAccessToServices = calculateAverage(reviews.stream().mapToDouble(Review::getAccessToServices));
                double averageSeatsTablesCounters = calculateAverage(reviews.stream().mapToDouble(Review::getSeatsTablesCounters));
                double averageRestRooms = calculateAverage(reviews.stream().mapToDouble(Review::getRestRooms));

                return new AddressRatingDto(address, overallRate, averageRateExperience, 
                                            averageEntrance, averageAccessToServices, 
                                            averageSeatsTablesCounters, averageRestRooms);
            })
            .sorted(Comparator.comparingDouble(AddressRatingDto::getOverallRate).reversed())
            .collect(Collectors.toList());
    }
    
    //limit the rates  to 3 decimal places
    private double calculateAverage(DoubleStream doubleStream) {
        OptionalDouble average = doubleStream.average();
        return average.isPresent() ? BigDecimal.valueOf(average.getAsDouble())
                                       .setScale(3, RoundingMode.HALF_UP)
                                       .doubleValue() 
                                   : 0.0;
    }
    
    //search for address on address_ratings page 
    public List<AddressRatingDto> searchAddressRatings(String searchTerm) {
        // Fetch all ratings
        List<AddressRatingDto> allRatings = getAddressRatings();

        // Filter based on the search term
        return allRatings.stream()
                         .filter(rating -> rating.getAddress().toLowerCase().contains(searchTerm.toLowerCase()))
                         .collect(Collectors.toList());
    }


}
