package com.yallauni.yalla.dto.ride;

public class RideResponseDTO {
    private Long id;
    private String origin;
    private String destination;
    private String date;
    // Προσθέστε και άλλα πεδία αν χρειάζεται

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

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