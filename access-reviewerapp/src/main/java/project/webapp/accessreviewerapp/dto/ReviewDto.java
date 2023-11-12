package project.webapp.accessreviewerapp.dto;

import java.util.List;

import org.springframework.web.multipart.MultipartFile;

public class ReviewDto {
    private String addressString;
    private int rateExperience;
    private int entrance;
    private int accessToServices;
    private int seatsTablesCounters;
    private int restRooms;
    private String comments;
    private Long userId;
    
    public Long getUserId() {
		return userId;
	}

	public void setUserId(Long userId) {
		this.userId = userId;
	}

	private List<MultipartFile> images;
    
    public List<MultipartFile> getImages() {
        return images;
    }

    public void setImages(List<MultipartFile> images) {
        this.images = images;
    }
    
    private String address; // Assuming address is a String type

    // ... other fields ...

    // Getter for the address field
    public String getAddress() {
        return address;
    }

    // Setter for the address field
    public void setAddress(String address) {
        this.address = address;
    }
    
    
	public String getAddressString() {
		return addressString;
	}
	public void setAddressString(String addressString) {
		this.addressString = addressString;
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
