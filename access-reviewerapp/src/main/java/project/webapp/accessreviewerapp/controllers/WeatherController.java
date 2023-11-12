package project.webapp.accessreviewerapp.controllers;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import project.webapp.accessreviewerapp.dto.WeatherResponse;
import project.webapp.accessreviewerapp.service.WeatherService;

@RestController
public class WeatherController {
	private final WeatherService weatherService;
	
	public WeatherController(WeatherService weatherService) {
		this.weatherService = weatherService;
	}
	
	@GetMapping("/weather")
	public WeatherResponse getWeather(@RequestParam("lat") double latitude, @RequestParam("lon") double longitude) {
		return weatherService.getWeatherData(latitude, longitude);
	}

}

