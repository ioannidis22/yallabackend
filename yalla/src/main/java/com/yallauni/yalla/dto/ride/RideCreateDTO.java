package com.yallauni.yalla.dto.ride;

import jakarta.validation.constraints.*;

public class RideCreateDTO {

    @NotBlank
    private String origin;

    @NotBlank
    private String destination;

    @NotBlank
    private String date;



    public String getOrigin() {
        return origin;
    }

    public void setOrigin(String origin) {
        this.origin = origin;
    }

    public String getDestination() {
        return destination;
    }

    public void setDestination(String destination) {
        this.destination = destination;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }
}