package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.VenueTimeDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.VenueTimeService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/venue-times")
public class VenueTimeController {
	
	@Autowired
	private VenueTimeService venueTimeService;
	
	@GetMapping("/venue/{venueId}")
	public ResponseEntity<ApiResponse<List<VenueTimeDto>>> getTimeSlotsByVenue(@PathVariable Integer venueId) {
		List<VenueTimeDto> venueTimeDtos = venueTimeService.getTimeSlotsByVenue(venueId);
		if (venueTimeDtos == null || venueTimeDtos.isEmpty()) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "查無對應時段"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", venueTimeDtos));
	}

}
