package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.TimeSlotDto;

public interface TimeSlotService {

	// 查詢時段
	List<TimeSlotDto> getAllTimeSlots();
}
