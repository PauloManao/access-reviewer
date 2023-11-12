package project.webapp.accessreviewerapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import project.webapp.accessreviewerapp.entities.Address;
import project.webapp.accessreviewerapp.repositories.AddressRepository;

@Service
public class AddressService {
	
	@Autowired
    private AddressRepository addressRepository;
	
    public Address getAddressByString(String address) {
        // Use orElse to return null in case the Optional is empty
        return addressRepository.findByAddress(address).orElse(null);
    }

    @Autowired
    public AddressService(AddressRepository addressRepository) {
        this.addressRepository = addressRepository;
    }

    public Address saveAddress(Address address) {
        return addressRepository.save(address);
    }

    public Address findByAddress(String addressString) {
        return addressRepository.findByAddress(addressString).orElse(null);
    }
        
}
