package project.webapp.accessreviewerapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import project.webapp.accessreviewerapp.entities.User;
import project.webapp.accessreviewerapp.repositories.UserRepository;

@Service
public class CustomUserDetailsService implements UserDetailsService{
	
	@Autowired
	private UserRepository userRepository;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		User user = userRepository.findByEmail(username);
		
		if (user == null) {
			throw new UsernameNotFoundException("user not found");
		}
		
        if (!user.isEnabled()) {
            throw new DisabledException("User is disabled");
        }
		
		return new CustomUserDetail(user);	
	}

}
