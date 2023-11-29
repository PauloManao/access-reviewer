package project.webapp.accessreviewerapp.service;

import java.util.List;
import java.util.stream.Collectors;

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
    
    
    @Override
    public User update(UserDto userDto, Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.setEmail(userDto.getEmail());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setRole(userDto.getRole());
        user.setUsername(userDto.getUsername());
        return userRepository.save(user);
    }

    @Override
    public void delete(Long id) {
        userRepository.deleteById(id);
    }

    @Override
    public List<UserDto> findAll() {
        return userRepository.findAll().stream().map(user -> {
            UserDto dto = new UserDto();
            dto.setId(user.getId()); // set the id
            dto.setEmail(user.getEmail());
            dto.setPassword(user.getPassword());
            dto.setRole(user.getRole());
            dto.setUsername(user.getUsername());
            return dto;
        }).collect(Collectors.toList());
    }

    @Override
    public UserDto findById(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        UserDto userDto = new UserDto(user.getEmail(), user.getPassword(), user.getRole(), user.getUsername());
        userDto.setId(user.getId()); // Make sure this line is setting the ID
        return userDto;
    }
    
    @Override
    public boolean emailExists(String email) {
        return userRepository.findByEmail(email) != null;
    }

}
