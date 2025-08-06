package com.example.demo.mapper;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.stream.Collectors;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.example.demo.model.dto.BookingDto;
import com.example.demo.model.dto.BookingRequest;
import com.example.demo.model.dto.TimeSlotDto;
import com.example.demo.model.dto.VenueDto;
import com.example.demo.model.dto.VenueTimeDto;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.TimeSlot;
import com.example.demo.model.entity.Venue;
import com.example.demo.model.entity.VenueTime;

@Component
public class DtoMapper {
	
	@Autowired
	private ModelMapper modelMapper;
	
	// booking 轉 bookingDto
	public BookingDto toBookingDto(Booking booking) {
        BookingDto dto = modelMapper.map(booking, BookingDto.class);
        dto.setVenueId(booking.getVenue().getId());
        dto.setTimeSlotId(booking.getTimeSlot().getId());
        dto.setCreatedAt(booking.getCreatedAt().format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));
        dto.setVenueName(booking.getVenue().getName()); 
        dto.setVenuePrice(booking.getVenue().getPrice());
        dto.setLabel(booking.getTimeSlot().getLabel());
        return dto;
    }

	// bookingDto 轉 booking
    public Booking toBookingEntity(BookingRequest request, Venue venue, TimeSlot timeSlot) {
        Booking booking = new Booking();
        booking.setVenue(venue);
        booking.setTimeSlot(timeSlot);
        booking.setBookingDate(request.getBookingDate());
        booking.setRenterName(request.getRenterName());
        booking.setRenterEmail(request.getRenterEmail());
        booking.setRenterPhone(request.getRenterPhone());
        booking.setIsPaid(false);
        booking.setCreatedAt(LocalDateTime.now());
        return booking;
    }

    // venue 轉 venueDto
    public VenueDto toVenueDto(Venue venue) {
        return modelMapper.map(venue, VenueDto.class);
    }

    // timeSlot 轉 timeSlotDto
    public TimeSlotDto toTimeSlotDto(TimeSlot timeSlot) {
        return modelMapper.map(timeSlot, TimeSlotDto.class);
    }

    // venueTime 轉 venueTimeDto
    public VenueTimeDto toVenueTimeDto(VenueTime vt) {
        VenueTimeDto dto = new VenueTimeDto();
        dto.setId(vt.getTimeSlot().getId());
        dto.setVenueId(vt.getVenue().getId());
        dto.setTimeSlotId(vt.getTimeSlot().getId());
        dto.setTimeLabel(vt.getTimeSlot().getLabel());
        return dto;
    }

    public List<BookingDto> toBookingDtoList(List<Booking> bookings) {
        return bookings.stream().map(this::toBookingDto).collect(Collectors.toList());
    }

    public List<VenueDto> toVenueDtoList(List<Venue> venues) {
        return venues.stream().map(this::toVenueDto).collect(Collectors.toList());
    }

    public List<TimeSlotDto> toTimeSlotDtoList(List<TimeSlot> slots) {
        return slots.stream().map(this::toTimeSlotDto).collect(Collectors.toList());
    }

    public List<VenueTimeDto> toVenueTimeDtoList(List<VenueTime> vts) {
        return vts.stream().map(this::toVenueTimeDto).collect(Collectors.toList());
    }

}
