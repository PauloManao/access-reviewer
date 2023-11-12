package project.webapp.accessreviewerapp.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import project.webapp.accessreviewerapp.dto.Geolocation;

@Service
public class GeocodingService {

    private final String NOMINATIM_URL = "https://nominatim.openstreetmap.org/search?format=json&limit=10&q=";

    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    public GeocodingService(RestTemplate restTemplate, ObjectMapper objectMapper) {
        this.restTemplate = restTemplate;
        this.objectMapper = objectMapper;
    }

    public ResponseEntity<String> geocodeAddress(String query) {
        String url = NOMINATIM_URL + query;
        return restTemplate.getForEntity(url, String.class);
    }

    public Geolocation getLatitudeLongitude(String address) {
        String url = NOMINATIM_URL + address;
        ResponseEntity<String> response = restTemplate.getForEntity(url, String.class);

        // Parse the JSON response to extract latitude and longitude
        try {
            JsonNode root = objectMapper.readTree(response.getBody());

            if (root.isArray() && root.size() > 0) {
                JsonNode firstResult = root.get(0);
                double latitude = firstResult.get("lat").asDouble();
                double longitude = firstResult.get("lon").asDouble();

                return new Geolocation(latitude, longitude);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        return null; // Return null if the parsing fails or address not found
    }
}
