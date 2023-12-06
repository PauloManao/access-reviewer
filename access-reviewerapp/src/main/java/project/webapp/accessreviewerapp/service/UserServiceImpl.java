package project.webapp.accessreviewerapp.service;

import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import project.webapp.accessreviewerapp.dto.UserDto;
import project.webapp.accessreviewerapp.entities.PasswordResetToken;
import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.repositories.PasswordResetTokenRepository;
import project.webapp.accessreviewerapp.repositories.UserRepository;

@Service
public class UserServiceImpl implements UserService{
	
	@Autowired
	private PasswordEncoder passwordEncoder;
	
	@Autowired
	private UserRepository userRepository;
	
    @Autowired
    private PasswordResetTokenRepository passwordResetTokenRepository;



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
            dto.setEnabled(user.isEnabled());
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
    
    @Override
    public User findByEmail(String email) {
        // Assuming userRepository has a method to find a user by email
        return userRepository.findByEmail(email);
    }
    
    @Override
    public void toggleEnabled(Long id) {
        User user = userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found"));
        user.toggleEnabled();
        userRepository.save(user);
    }
    
    public String createPasswordResetToken(User user) {
        // Check for existing token
        PasswordResetToken existingToken = passwordResetTokenRepository.findByUser(user);
        
        if (existingToken != null) {
            // Delete existing token
            passwordResetTokenRepository.delete(existingToken);
        }

        // Create new token
        String token = UUID.randomUUID().toString();
        PasswordResetToken newToken = new PasswordResetToken(user, token, calculateExpiryDate(3 * 60)); // 3 hours
        passwordResetTokenRepository.save(newToken);

        return token;
    }

    private Date calculateExpiryDate(int expiryTimeInMinutes) {
        Calendar cal = Calendar.getInstance();
        cal.setTimeInMillis(new Date().getTime());
        cal.add(Calendar.MINUTE, expiryTimeInMinutes);
        return new Date(cal.getTime().getTime());
    }

    public boolean isResetTokenValid(String token) {
        PasswordResetToken passToken = passwordResetTokenRepository.findByToken(token);
        return passToken != null && passToken.getExpiryDate().after(new Date());
    }

    public void updatePassword(String token, String newPassword) {
        PasswordResetToken resetToken = passwordResetTokenRepository.findByToken(token);
        User user = resetToken.getUser();
        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
    }

}
