package project.webapp.accessreviewerapp.service;

import project.webapp.accessreviewerapp.dto.UserDto;
import project.webapp.accessreviewerapp.entities.User;

public interface UserService {
	
	User save(UserDto userDto);
}
