package project.webapp.accessreviewerapp.controllers;

import java.security.Principal;
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

import project.webapp.accessreviewerapp.dto.ReviewDto;
import project.webapp.accessreviewerapp.dto.UserDto;
import project.webapp.accessreviewerapp.entities.Address;
import project.webapp.accessreviewerapp.entities.Review;
import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.repositories.AddressRepository;
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
    
    // Endpoint to save a review (for non-image reviews)
    @PostMapping("/save")
    public ResponseEntity<String> saveReview(@RequestBody Review review) {
        // Ensure the address exists in the database
        Address existingAddress = addressRepository.save(review.getAddress());
        review.setAddress(existingAddress);

        reviewService.saveReview(review); 
        return ResponseEntity.ok("Review submitted successfully with ID: " + review.getId());
    }
    
    // New endpoint for submitting a review with images
    @PostMapping("/submit")
    public ResponseEntity<String> submitReview(@ModelAttribute ReviewDto reviewDto,
                                               @RequestParam("images") List<MultipartFile> images,
                                               Principal principal) {
    	
    	  String email = principal.getName(); // This should give you the username (email in your case)
    	    User user = userRepository.findByEmail(email);
    	    
    	    if (user == null) {
    	        throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, "User not found");
    	    }
    	    
    	    reviewDto.setUserId(user.getId()); // Set the user ID from the fetched user

    	    try {
    	        Review review = reviewService.submitReview(reviewDto.getAddressString(), reviewDto, images);
    	        return ResponseEntity.ok("Review submitted successfully with ID: " + review.getId());
    	    } catch (Exception e) {
    	        // Log the exception and handle it as per your application's requirement
    	        e.printStackTrace();
    	        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Error occurred: " + e.getMessage());
    	    }
    }
    
    //check the comments in database to display them in the details page
    // Endpoint to get comments by address ID
    @GetMapping("/reviews/comments/address/{addressId}")
    public ResponseEntity<List<String>> getCommentsByAddress(@PathVariable Long addressId) {
        List<String> comments = reviewService.getCommentsByAddressId(addressId);
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    //New endpoint to get comments by address string
    @GetMapping("/comments")
    public ResponseEntity<List<String>> getCommentsByAddressString(@RequestParam("addressString") String addressString) {
        Optional<Address> address = addressRepository.findByAddress(addressString);
               
        if (!address.isPresent()) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, "Address not found");
        }
        
        List<String> comments = reviewService.getCommentsByAddressId(address.get().getId());
        
        if (comments.isEmpty()) {
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        
        return new ResponseEntity<>(comments, HttpStatus.OK);
    }
    
    //list reviews in the review page
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
    
    //edit reviews

    
    //delete reviews
    @GetMapping("/reviews_list/delete/{id}")
    public String deleteReview(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        reviewService.deleteReview(id);
        redirectAttributes.addFlashAttribute("reviewerMessage", "Review deleted successfully with ID: " + id);
        return "redirect:/reviews_list";
    }
        
}
