package com.example.demo.service;

import java.util.List;

import com.example.demo.model.dto.VenueDto;

public interface VenueService {

	// 查詢所有場地
	List<VenueDto> getAllVenues();
	
	// 根據 ID 取得場地詳細資料
	VenueDto getVenueById(Integer id);
	
	// 根據場地名稱查詢場地 (模糊比對)
	List<VenueDto> venueByName(String name);
}
