package project.webapp.accessreviewerapp.dto;

public class AddressRatingDto {
    private String address;
    private double overallRate;
    private double averageRateExperience;
    private double averageEntrance;
    private double averageAccessToServices;
    private double averageSeatsTablesCounters;
    private double averageRestRooms;
	public String getAddress() {
		return address;
	}
	public void setAddress(String address) {
		this.address = address;
	}
	public double getOverallRate() {
		return overallRate;
	}
	public void setOverallRate(double overallRate) {
		this.overallRate = overallRate;
	}
	public double getAverageRateExperience() {
		return averageRateExperience;
	}
	public void setAverageRateExperience(double averageRateExperience) {
		this.averageRateExperience = averageRateExperience;
	}
	public double getAverageEntrance() {
		return averageEntrance;
	}
	public void setAverageEntrance(double averageEntrance) {
		this.averageEntrance = averageEntrance;
	}
	public double getAverageAccessToServices() {
		return averageAccessToServices;
	}
	public void setAverageAccessToServices(double averageAccessToServices) {
		this.averageAccessToServices = averageAccessToServices;
	}
	public double getAverageSeatsTablesCounters() {
		return averageSeatsTablesCounters;
	}
	public void setAverageSeatsTablesCounters(double averageSeatsTablesCounters) {
		this.averageSeatsTablesCounters = averageSeatsTablesCounters;
	}
	public double getAverageRestRooms() {
		return averageRestRooms;
	}
	public void setAverageRestRooms(double averageRestRooms) {
		this.averageRestRooms = averageRestRooms;
	}
	
	
	public AddressRatingDto() {}
	
	public AddressRatingDto(String address, double overallRate, double averageRateExperience, double averageEntrance,
			double averageAccessToServices, double averageSeatsTablesCounters, double averageRestRooms) {
		super();
		this.address = address;
		this.overallRate = overallRate;
		this.averageRateExperience = averageRateExperience;
		this.averageEntrance = averageEntrance;
		this.averageAccessToServices = averageAccessToServices;
		this.averageSeatsTablesCounters = averageSeatsTablesCounters;
		this.averageRestRooms = averageRestRooms;
	}
    
    
}
