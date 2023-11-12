package project.webapp.accessreviewerapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import project.webapp.accessreviewerapp.dto.UserDto;
import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public User save(UserDto userDto) {
		
		User user = new User(userDto.getEmail(), passwordEncoder.encode(userDto.getPassword()), userDto.getRole(), userDto.getUsername());
		
		return userRepository.save(user);
	}
	
    public UserDto getEmptyUserDto() {
        return new UserDto(); // Return a new, empty UserDto object
    }

}
