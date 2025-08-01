package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.VenueTimeDto;

public interface VenueTimeService {

	// 根據 ID 查詢該場地支援的時段
	List<VenueTimeDto> getTimeSlotsByVenue(Integer venueId);
}
