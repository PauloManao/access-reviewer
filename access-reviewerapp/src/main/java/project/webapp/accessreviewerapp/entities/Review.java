package project.webapp.accessreviewerapp.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;

@Entity
public class Review {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne
    @JoinColumn(name = "address_id")
    private Address address;
    
    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;
    
    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    private int rateExperience;

    private int entrance;

    private int accessToServices;

    private int seatsTablesCounters;

    private int restRooms;

    private String comments;
    
    private LocalDateTime submissionDate;
    
    private boolean isEnabled = true;

    // Getter and Setter for isEnabled
    public boolean isEnabled() {
        return isEnabled;
    }

    public void setEnabled(boolean isEnabled) {
        this.isEnabled = isEnabled;
    }

    // Toggle function
    public void toggleEnabled() {
        this.isEnabled = !this.isEnabled;
    }
    
    public LocalDateTime getSubmissionDate() {
        return submissionDate;
    }

    public void setSubmissionDate(LocalDateTime submissionDate) {
        this.submissionDate = submissionDate;
    }
    
    public Review() {
        // No-arguments constructor needed by JPA
    }
    

	public Review(Long id, Address address, int rateExperience, int entrance, int accessToServices,
			int seatsTablesCounters, int restRooms, String comments, User user, LocalDateTime submissionDate) {
		super();
		this.id = id;
		this.address = address;
		this.rateExperience = rateExperience;
		this.entrance = entrance;
		this.accessToServices = accessToServices;
		this.seatsTablesCounters = seatsTablesCounters;
		this.restRooms = restRooms;
		this.comments = comments;
		this.user = user;
		this.submissionDate = submissionDate;
	}
	
	
	
	@OneToMany(mappedBy = "review", cascade = CascadeType.ALL, orphanRemoval = true)
	private List<Image> images = new ArrayList<>();
	
	public List<Image> getImages() {
	    return images;
	}

	public void setImages(List<Image> images) {
	    this.images = images;
	}

	public void addImage(Image image) {
	    images.add(image);
	    image.setReview(this);
	}

	public void removeImage(Image image) {
	    images.remove(image);
	    image.setReview(null);
	}
	
	
	

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}

	public int getRateExperience() {
		return rateExperience;
	}

	public void setRateExperience(int rateExperience) {
		this.rateExperience = rateExperience;
	}

	public int getEntrance() {
		return entrance;
	}

	public void setEntrance(int entrance) {
		this.entrance = entrance;
	}

	public int getAccessToServices() {
		return accessToServices;
	}

	public void setAccessToServices(int accessToServices) {
		this.accessToServices = accessToServices;
	}

	public int getSeatsTablesCounters() {
		return seatsTablesCounters;
	}

	public void setSeatsTablesCounters(int seatsTablesCounters) {
		this.seatsTablesCounters = seatsTablesCounters;
	}

	public int getRestRooms() {
		return restRooms;
	}

	public void setRestRooms(int restRooms) {
		this.restRooms = restRooms;
	}

	public String getComments() {
		return comments;
	}

	public void setComments(String comments) {
		this.comments = comments;
	}
    
    

}
