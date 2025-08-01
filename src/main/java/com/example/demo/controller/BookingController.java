package com.example.demo.controller;

import java.time.LocalDate;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.demo.model.dto.BookingDto;
import com.example.demo.model.dto.BookingRequest;
import com.example.demo.response.ApiResponse;
import com.example.demo.service.BookingService;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api/bookings")
public class BookingController {

	@Autowired
	private BookingService bookingService;
	
	@GetMapping
	public ResponseEntity<ApiResponse<List<BookingDto>>> getAllBookings() {
		List<BookingDto> bookingDtos = bookingService.getAllBookings();
		return ResponseEntity.ok(ApiResponse.success("查詢成功", bookingDtos));
	}
	
	@PostMapping
	public ResponseEntity<ApiResponse<BookingDto>> createBooting(@RequestBody BookingRequest request) {
		BookingDto bookingDto = bookingService.createBooking(request);
		if (bookingDto != null) {
			return ResponseEntity.ok(ApiResponse.success("預約成功", bookingDto));
		}
		return ResponseEntity.status(404).body(ApiResponse.error(404, "預約失敗"));
	}
	
	@PutMapping("/{id}")
	public ResponseEntity<ApiResponse<BookingDto>> updateBooking(@PathVariable Integer id, @RequestBody BookingRequest request) {
		BookingDto bookingDto = bookingService.updateBooking(id, request);
		if (bookingDto != null) {
			return ResponseEntity.ok(ApiResponse.success("更新成功", bookingDto));
		}
		return ResponseEntity.status(404).body(ApiResponse.error(404, "更新失敗"));
	}
	
	@DeleteMapping("/{id}")
	public ResponseEntity<ApiResponse<Void>> deleteBooking(@PathVariable Integer id) {
		try {
			bookingService.deleteBooking(id);
			return ResponseEntity.ok(ApiResponse.success("取消成功", null));
		} catch (Exception e) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "取消失敗"));
		}
	}
	
	@GetMapping(params = "email")
	public ResponseEntity<ApiResponse<List<BookingDto>>> getBookingByEmail(@RequestParam String email) {
		List<BookingDto> bookingDtos = bookingService.getBookingsByEmail(email);
		if ( bookingDtos == null || bookingDtos.isEmpty()) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "查無資料"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", bookingDtos));
	}
	
	@GetMapping(params = "name")
	public ResponseEntity<ApiResponse<List<BookingDto>>> getBookingByName(@RequestParam String name) {
		List<BookingDto> bookingDtos = bookingService.getBookingsByName(name);
		if ( bookingDtos == null || bookingDtos.isEmpty()) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "查無資料"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", bookingDtos));
	}
	
	@GetMapping(params = "phone")
	public ResponseEntity<ApiResponse<List<BookingDto>>> getBookingByPhone(@RequestParam String phone) {
		List<BookingDto> bookingDtos = bookingService.getBookingsByPhone(phone);
		if ( bookingDtos == null || bookingDtos.isEmpty()) {
			return ResponseEntity.status(404).body(ApiResponse.error(404, "查無資料"));
		}
		return ResponseEntity.ok(ApiResponse.success("查詢成功", bookingDtos));
	}
	
	@GetMapping("/venue/{venueId}/date/{date}")
	public ResponseEntity<ApiResponse<List<BookingDto>>> getBookingByDate (@PathVariable Integer venueId, @PathVariable String date) {
		List<BookingDto> bookingDtos = bookingService.getVenueBookingsByDate(venueId, LocalDate.parse(date));
		// 回傳 200，即使沒資料，也告訴前端「查詢成功但無結果」
	    return ResponseEntity.ok(ApiResponse.success("查詢成功", bookingDtos));
	}
}
