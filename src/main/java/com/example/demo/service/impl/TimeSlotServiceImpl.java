package com.example.demo.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.DtoMapper;
import com.example.demo.model.dto.TimeSlotDto;
import com.example.demo.repository.TimeSlotRepository;
import com.example.demo.service.TimeSlotService;

@Service
public class TimeSlotServiceImpl implements TimeSlotService {

	@Autowired
	private TimeSlotRepository timeSlotRepository;
	
	@Autowired
	private DtoMapper dtoMapper;

	@Override
	public List<TimeSlotDto> getAllTimeSlots() {
		return timeSlotRepository.findAll()
				.stream()
				.map(dtoMapper :: toTimeSlotDto)
				.collect(Collectors.toList()); // 回傳所有時段
	}
}
