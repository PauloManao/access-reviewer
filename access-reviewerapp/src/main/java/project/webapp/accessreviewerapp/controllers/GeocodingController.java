package project.webapp.accessreviewerapp.controllers;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import project.webapp.accessreviewerapp.service.GeocodingService;

@RestController
public class GeocodingController {
	private final GeocodingService geocodingService;
	
	public GeocodingController(GeocodingService geocodingService) {
		this.geocodingService = geocodingService;
	}
	
	@GetMapping("/geocode")
	public ResponseEntity<String> geocode(@RequestParam String query){
		return geocodingService.geocodeAddress(query);
	}
	

}
