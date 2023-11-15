package project.webapp.accessreviewerapp.service;

import java.util.List;

import project.webapp.accessreviewerapp.dto.UserDto;
import project.webapp.accessreviewerapp.entities.User;

public interface UserService {
	
	User save(UserDto userDto);
	User update(UserDto userDto, Long id);
	void delete(Long id);
    List<UserDto> findAll();
    UserDto findById(Long id);
}
