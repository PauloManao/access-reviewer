package project.webapp.accessreviewerapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Weather {
    @JsonProperty("description")
    private String description;

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    
}