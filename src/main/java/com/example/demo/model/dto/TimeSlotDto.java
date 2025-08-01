package com.example.demo.model.dto;

import lombok.Data;

@Data
public class TimeSlotDto {
    private Integer id;
    private String startTime;
    private String endTime;
    private String label;
}