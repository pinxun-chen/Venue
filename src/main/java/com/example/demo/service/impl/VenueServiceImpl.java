package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.DtoMapper;
import com.example.demo.model.dto.VenueDto;
import com.example.demo.model.entity.Venue;
import com.example.demo.repository.VenueRepository;
import com.example.demo.service.VenueService;

@Service
public class VenueServiceImpl implements VenueService {

	@Autowired
	private VenueRepository venueRepository;
	
	@Autowired
	private DtoMapper dtoMapper;
	
	@Override
	public List<VenueDto> getAllVenues() {
		return venueRepository.findAll()
				.stream()
				.map(dtoMapper :: toVenueDto)
				.collect(Collectors.toList()); // 回傳所有場地
	}

	@Override
	public VenueDto getVenueById(Integer id) {
		Venue venue = venueRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("找不到該場地"));
		return dtoMapper.toVenueDto(venue);
	}

	@Override
	public List<VenueDto> venueByName(String name) {
		List<Venue> venues = venueRepository.findByNameContainingIgnoreCase(name);
		return venues.stream()
				.map(dtoMapper :: toVenueDto)
				.collect(Collectors.toList());
	}

}
