package project.webapp.accessreviewerapp.controllers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;

import project.webapp.accessreviewerapp.dto.WeatherResponse;
import project.webapp.accessreviewerapp.service.WeatherService;

@Controller
public class DetailsController {

    private final WeatherService weatherService;

    @Autowired
    public DetailsController(WeatherService weatherService) {
        this.weatherService = weatherService;
    }

    @GetMapping("/details.html")
    public String details(
            @RequestParam("address") String address,
            @RequestParam("lat") double latitude,
            @RequestParam("lon") double longitude,
            Model model
    ) {
        model.addAttribute("address", address);

        // Fetch weather information based on latitude and longitude using WeatherService
        WeatherResponse weatherResponse = weatherService.getWeatherData(latitude, longitude);

        // Add weather data to the model
        model.addAttribute("weatherData", weatherResponse);

        // Return the "details" template
        return "details";
    }
}
