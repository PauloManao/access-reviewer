package project.webapp.accessreviewerapp.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import project.webapp.accessreviewerapp.dto.WeatherResponse;

@Service
public class WeatherService {
    private final WeatherApiConfig apiConfig;
    private final RestTemplate restTemplate;

    @Autowired
    public WeatherService(WeatherApiConfig apiConfig, RestTemplate restTemplate) {
        this.apiConfig = apiConfig;
        this.restTemplate = restTemplate;
    }

    public WeatherResponse getWeatherData(double latitude, double longitude) {
        String apiKey = apiConfig.getKey();
        String apiUrl = apiConfig.getUrl() + "?lat=" + latitude + "&lon=" + longitude + "&appid=" + apiKey;

        return restTemplate.getForObject(apiUrl, WeatherResponse.class);
    }
    
}
