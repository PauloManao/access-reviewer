package project.webapp.accessreviewerapp.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

public class Main {
    @JsonProperty("temp")
    private double temp;

    public double getTemp() {
        return temp;
    }

    public void setTemp(double temp) {
        this.temp = temp;
    }
    
}