package project.webapp.accessreviewerapp.dto;

public class UserDto {
	
	private String email;
	private String password;
	private String role;
	private String username;
	
	public UserDto(String email, String password, String role, String username) {
		super();
		this.email = email;
		this.password = password;
		this.role = role;
		this.username = username;
	}
	
	public UserDto() {
		super();

	}
	


	public String getEmail() {
		return email;
	}


	public void setEmail(String email) {
		this.email = email;
	}


	public String getPassword() {
		return password;
	}


	public void setPassword(String password) {
		this.password = password;
	}


	public String getRole() {
		return role;
	}


	public void setRole(String role) {
		this.role = role;
	}


	public String getUsername() {
		return username;
	}


	public void setUsername(String username) {
		this.username = username;
	}
	
	

}
