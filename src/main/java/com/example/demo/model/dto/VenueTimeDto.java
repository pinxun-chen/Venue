package com.example.demo.model.dto;

import lombok.Data;

@Data
public class VenueTimeDto {
    private Integer id;
    private Integer venueId;
    private Integer timeSlotId;
    private String timeLabel;
}