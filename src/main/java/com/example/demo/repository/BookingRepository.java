package com.example.demo.repository;

import java.time.LocalDate;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Booking;

public interface BookingRepository extends JpaRepository<Booking, Integer> {

	// 用 email 查詢預約紀錄
	List<Booking> findByRenterEmailContaining(String email);
	
	// 用姓名查詢預約人
	List<Booking> findByRenterNameContaining(String name);
	
	// 用電話查詢預約人
	List<Booking> findByRenterPhoneContaining(String phone);
	
	// 查詢場地所有預約
	List<Booking> findByVenueIdAndBookingDate(Integer venueId, LocalDate bookingDate);
}
