package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.TimeSlotDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.TimeSlotService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/times")
public class TimeSlotController {

	@Autowired
	private TimeSlotService timeSlotService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<TimeSlotDto>>> getAllTimeSlots() {
		List<TimeSlotDto> slotDtos = timeSlotService.getAllTimeSlots();
		if (slotDtos == null || slotDtos.isEmpty()) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "查無可用時段"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", slotDtos));
	}
}
