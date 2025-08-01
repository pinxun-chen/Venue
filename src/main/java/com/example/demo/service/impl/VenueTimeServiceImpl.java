package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.DtoMapper;
import com.example.demo.model.dto.VenueTimeDto;
import com.example.demo.model.entity.VenueTime;
import com.example.demo.repository.VenueTimeRepository;
import com.example.demo.service.VenueTimeService;

@Service
public class VenueTimeServiceImpl implements VenueTimeService {

	@Autowired
	private VenueTimeRepository venueTimeRepository;
	
	@Autowired
	private DtoMapper dtoMapper;
	
	@Override
	public List<VenueTimeDto> getTimeSlotsByVenue(Integer venueId) {
		List<VenueTime> list = venueTimeRepository.findByVenueId(venueId);
		return list.stream()
				.map(dtoMapper :: toVenueTimeDto)
				.collect(Collectors.toList());
	}

}
