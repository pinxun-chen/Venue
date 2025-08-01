package com.example.demo.service;

import java.time.LocalDate;
import java.util.List;

import com.example.demo.model.dto.BookingDto;
import com.example.demo.model.dto.BookingRequest;

public interface BookingService {

	// 查詢全部預約
	List<BookingDto> getAllBookings();
	
	// 建立新的預約紀錄
	BookingDto createBooking(BookingRequest request);
	
	// 更新預約內容
	BookingDto updateBooking(Integer id, BookingRequest request);
	
	// 刪除預約
	void deleteBooking(Integer id);
	
	// 透過 Email 查詢預約紀錄
	List<BookingDto> getBookingsByEmail(String email);
	
	// 透過姓名查詢預約紀錄
	List<BookingDto> getBookingsByName(String name);
	
	// 透過電話查詢預約紀錄
	List<BookingDto> getBookingsByPhone(String phone);
	
	// 查詢場地已被預約的紀錄
	List<BookingDto> getVenueBookingsByDate(Integer venueId, LocalDate date);
	
}
