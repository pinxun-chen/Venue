package com.example.demo.service.impl;

import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.example.demo.mapper.DtoMapper;
import com.example.demo.model.dto.BookingDto;
import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.TimeSlot;
import com.example.demo.model.entity.Venue;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.TimeSlotRepository;
import com.example.demo.repository.VenueRepository;
import com.example.demo.service.BookingService;

@Service
public class BookingServiceImpl implements BookingService {

	@Autowired
	private BookingRepository bookingRepository;
	
	@Autowired
	private VenueRepository venueRepository;
	
	@Autowired
	private TimeSlotRepository timeSlotRepository;
	
	@Autowired
	private DtoMapper dtoMapper;
	
	@Override
	public List<BookingDto> getAllBookings() {
		List<Booking> list = bookingRepository.findAll();
		return list.stream()
				.map(dtoMapper :: toBookingDto)
				.collect(Collectors.toList());
	}
	
	@Override
	public BookingDto createBooking(BookingRequest request) {
		Venue venue = venueRepository.findById(request.getVenueId())
				.orElseThrow(() -> new RuntimeException("找不到場地"));
		
		TimeSlot timeSlot = timeSlotRepository.findById(request.getTimeSlotId())
				.orElseThrow(() -> new RuntimeException("找不到時段"));
		
		Booking booking = dtoMapper.toBookingEntity(request, venue, timeSlot);
		Booking saved = bookingRepository.save(booking); 
		return dtoMapper.toBookingDto(saved); // 建立新的預約
	}

	@Override
	public BookingDto updateBooking(Integer id, BookingRequest request) {
		Booking booking = bookingRepository.findById(id)
				.orElseThrow(() -> new RuntimeException("預約不存在"));
		
		booking.setRenterName(request.getRenterName());
		booking.setRenterEmail(request.getRenterEmail());
		booking.setRenterPhone(request.getRenterPhone());
		booking.setBookingDate(request.getBookingDate());
		
		Booking upDateBooking = bookingRepository.save(booking); 
		
		return dtoMapper.toBookingDto(upDateBooking);
	}

	@Override
	public void deleteBooking(Integer id) {
		Booking booking = bookingRepository.findById(id)
		        .orElseThrow(() -> new RuntimeException("預約不存在"));

	    if (Boolean.TRUE.equals(booking.getIsPaid())) {
	        throw new RuntimeException("已付款的預約不得取消");
	    }
	    
		bookingRepository.deleteById(id); // 刪除預約
	}

	@Override
	public List<BookingDto> getBookingsByEmail(String email) {
		return bookingRepository.findByRenterEmailContaining(email)
				.stream()
				.map(dtoMapper :: toBookingDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<BookingDto> getBookingsByName(String name) {
		return bookingRepository.findByRenterNameContaining(name)
				.stream()
				.map(dtoMapper::toBookingDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<BookingDto> getBookingsByPhone(String phone) {
		return bookingRepository.findByRenterPhoneContaining(phone)
				.stream()
				.map(dtoMapper::toBookingDto)
				.collect(Collectors.toList());
	}

	@Override
	public List<BookingDto> getVenueBookingsByDate(Integer venueId, LocalDate date) {
		return bookingRepository.findByVenueIdAndBookingDate(venueId, date)
				.stream()
				.map(dtoMapper::toBookingDto)
				.collect(Collectors.toList());
	}

}
