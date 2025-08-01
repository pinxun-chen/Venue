package com.example.demo.model.dto;

import java.time.LocalDate;

import lombok.Data;

@Data
public class BookingDto {
	private Integer id;
	private Integer venueId;
	private Integer timeSlotId;
	private LocalDate bookingDate;
	private String renterName;
	private String renterEmail;
	private String renterPhone;
	private Boolean isPaid;
	private String createdAt;
	private String venueName;
	private String label;
}
