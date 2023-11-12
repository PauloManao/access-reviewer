package project.webapp.accessreviewerapp.repositories;

import org.springframework.data.jpa.repository.JpaRepository;
import java.util.Optional;

import project.webapp.accessreviewerapp.entities.Address;

public interface AddressRepository extends JpaRepository<Address, Long>{
	
	Optional<Address> findByAddress(String address);
	
}
