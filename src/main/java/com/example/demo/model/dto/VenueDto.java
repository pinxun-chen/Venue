package com.example.demo.model.dto;

import lombok.Data;

@Data
public class VenueDto {
    private Integer id;
    private String name;
    private String location;
    private String description;
    private Integer price;
}