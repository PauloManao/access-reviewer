package project.webapp.accessreviewerapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class WeatherResponse {
    @JsonProperty("main")
    private Main main;
    @JsonProperty("weather")
    private Weather[] weather;

    public WeatherResponse() {
        // Default constructor
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

    public Weather[] getWeather() {
        return weather;
    }

    public void setWeather(Weather[] weather) {
        this.weather = weather;
    }
    
}
