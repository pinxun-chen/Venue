package com.example.demo.controller;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.VenueDto;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.VenueService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/venues")
public class VenueController {
	
	@Autowired
	private VenueService venueService;

	// 取得所有場地
	@GetMapping
	public ResponseEntity<ApiResponse<List<VenueDto>>> getAllOrByName(@RequestParam(required = false) String name){
		List<VenueDto> venues= (name != null) ? venueService.venueByName(name) : venueService.getAllVenues();
		if (venues == null || venues.isEmpty()) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "查無場地資料"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", venues));
	}
	
	// 取得場地詳細資料
	@GetMapping("/{id}")
	public ResponseEntity<ApiResponse<VenueDto>> getVenueById(@PathVariable Integer id) {
		VenueDto dto = venueService.getVenueById(id);
		if(dto != null) {
			return ResponseEntity.ok(ApiResponse.success("查詢成功", dto));
		}
		return ResponseEntity.status(404).body(ApiResponse.error(404, "查無場地資料"));
	}
	
	// 模糊比對搜尋場地
	@GetMapping(params = "name")
	public ResponseEntity<ApiResponse<List<VenueDto>>> getVenueByName(@RequestParam String name) {
		List<VenueDto> venueDtos = venueService.venueByName(name);
		if (venueDtos == null || venueDtos.isEmpty()) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "查無場地"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", venueDtos));
	}
}
