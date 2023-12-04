package project.webapp.accessreviewerapp.controllers;

import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional; // Make sure to import Optional

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import jakarta.persistence.EntityNotFoundException;
import project.webapp.accessreviewerapp.dto.ReportReviewRequest;
import project.webapp.accessreviewerapp.dto.ReviewDto;

import project.webapp.accessreviewerapp.entities.Address;
import project.webapp.accessreviewerapp.entities.Review;
import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.repositories.AddressRepository;
import project.webapp.accessreviewerapp.repositories.ReviewRepository;
import project.webapp.accessreviewerapp.repositories.UserRepository;
import project.webapp.accessreviewerapp.service.ReviewService;

@Controller
@RequestMapping
public class ReviewController {
	
    private final ReviewService reviewService;
    private final AddressRepository addressRepository; // Assuming you still need direct access to this repository
    private final UserRepository userRepository; // Add the UserRepository here

    // Single constructor injection is cleaner and recommended
    @Autowired
    public ReviewController(ReviewService reviewService, AddressRepository addressRepository, UserRepository userRepository) {
        this.reviewService = reviewService;
        this.addressRepository = addressRepository;
        this.userRepository = userRepository;
    }
    
    @Autowired
    private ReviewRepository reviewRepository;
    
    //report a review
    @PostMapping("/reportReview")
    public ResponseEntity<?> reportReview(@RequestBody ReportReviewRequest request) {
        reviewService.reportReview(request.getReviewId(), request.getReason());
        return ResponseEntity.ok("Review reported successfully");
    }
    
    // Endpoint to save a review (for non-image reviews)
    @PostMapping("/save")
    public String saveReview(@RequestBody Review review, RedirectAttributes redirectAttributes) {
        // Ensure the address exists in the database
        Address existingAddress = addressRepository.save(review.getAddress());
        review.setAddress(existingAddress);
        
        Review savedReview = reviewService.saveReview(review);
        redirectAttributes.addFlashAttribute("reviewId", savedReview.getId());
        return "redirect:/submitted_review";
    }
    
    // Submit a review with images
    @PostMapping("/submit")
    public String submitReview(@ModelAttribute ReviewDto reviewDto,
                               @RequestParam("images") List<MultipartFile> images,
                               Principal principal, RedirectAttributes redirectAttributes) {
        String email = principal.getName(); // Get username (email)
        User user = userRepository.findByEmail(email);

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
        }

        reviewDto.setUserId(user.getId()); // Set user ID

        try {
            Review review = reviewService.submitReview(reviewDto.getAddressString(), reviewDto, images);
            redirectAttributes.addFlashAttribute("reviewId", review.getId());
            return "redirect:/submitted_review";
        } catch (Exception e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("error", "Error occurred: " + e.getMessage());
            return "redirect:/error_page"; // Replace with your error page
        }
    }
    
    @GetMapping("/submitted_review")
    public String showSubmittedReview(Model model) {
        return "submitted_review"; 
    }
    
    
    //  get comments by address ID and display them on the details page
    @GetMapping("/comments")
    public ResponseEntity<List<ReviewDto>> getCommentsByAddressString(@RequestParam("addressString") String addressString) {
        Optional<Address> address = addressRepository.findByAddress(addressString);
                
        if (!address.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found");
        }
        
        List<ReviewDto> enabledComments = reviewService.getEnabledCommentsByAddressId(address.get().getId());
        
        if (enabledComments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        return new ResponseEntity<>(enabledComments, HttpStatus.OK);
    }
    
    //list reviews in the review management page
    @GetMapping("/reviews_list")
    public String listReviews(Model model) {
        List<ReviewDto> reviews = reviewService.findAll();
        model.addAttribute("reviews_list", reviews);
        return "reviews_list"; 
    }
    

	@GetMapping("/reviews_list/edit/{id}")
	public String editReviewForm(@PathVariable Long id, Model model) {
	    ReviewDto reviewDto = reviewService.findById(id);
	    model.addAttribute("review", reviewDto);	    
	    return "reviews_edit";
	}
    
    //update reviews
    @PostMapping("/reviews_list/update/{id}")
    public String updateReview(@PathVariable Long id, @ModelAttribute ReviewDto reviewDto, RedirectAttributes redirectAttributes) {
        reviewService.updateReview(id, reviewDto);
        redirectAttributes.addFlashAttribute("reviewerMessage", "Review updated successfully with id: "+ id);
        return "redirect:/reviews_list";
    }
    
 
    //delete reviews
    @GetMapping("/reviews_list/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.deleteReview(id);
        redirectAttributes.addFlashAttribute("reviewerMessage", "Review deleted successfully with ID: " + id);
        return "redirect:/reviews_list";
    }
    
    @PostMapping("/admin/reviews/enable/{id}")
    public String enableReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.toggleReviewEnabled(id);
        redirectAttributes.addFlashAttribute("reviewerMessage", "Review enabled successfully with ID: " + id);
        return "redirect:/reviews_list";
    }

    @PostMapping("/admin/reviews/disable/{id}")
    public String disableReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.toggleReviewEnabled(id);
        redirectAttributes.addFlashAttribute("reviewerMessage", "Review disabled successfully with ID: " + id);
        return "redirect:/reviews_list";
    }
    
    @GetMapping("/images")
    public ResponseEntity<List<String>> getImagesForAddress(@RequestParam("addressString") String addressString) {
        List<String> imageUrls = reviewService.getImageUrlsForAddress(addressString);
        return ResponseEntity.ok(imageUrls);
    }
    
    @GetMapping("/user_reviews")
    public String userReviews(Principal principal, Model model) {
        String email = principal.getName(); // Get the currently logged-in user's email
        User user = userRepository.findByEmail(email);
        if (user != null) {
            List<ReviewDto> userReviews = reviewService.findReviewsByUserId(user.getId());
            model.addAttribute("reviews_list", userReviews);
        } else {
            // Handle the case where the user is not found
            model.addAttribute("reviews_list", new ArrayList<>());
        }
        return "user_review"; // Name of the Thymeleaf template for user reviews
    }
    
    @PostMapping("/delete_review/{id}")
    public String deleteReview(@PathVariable("id") Long id, Principal principal, RedirectAttributes redirectAttributes) {
        User user = userRepository.findByEmail(principal.getName());
        Review review = reviewRepository.findById(id)
                        .orElseThrow(() -> new EntityNotFoundException("Review not found"));

        // Check if the review belongs to the logged-in user
        if (review.getUser().equals(user)) {
            reviewService.deleteReview(id);
            redirectAttributes.addFlashAttribute("successMessage", "Review deleted successfully");
        } else {
            redirectAttributes.addFlashAttribute("errorMessage", "You can't delete reviews that are not yours");
        }

        return "redirect:/user_reviews";
    }
        
}
