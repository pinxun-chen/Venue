package com.example.demo.service.impl;

import com.example.demo.model.dto.GetPaymentByKeyResp.PaymentData;
import com.example.demo.model.entity.Booking;
import com.example.demo.model.entity.Payment;
import com.example.demo.repository.BookingRepository;
import com.example.demo.repository.PaymentRepository;
import com.example.demo.service.PaymentService;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Service
public class PaymentServiceImpl implements PaymentService {

    @Autowired
    private PaymentRepository paymentRepository;

    @Autowired
    private BookingRepository bookingRepository;

    @Override
    @Transactional
    public void savePaymentRecord(PaymentData data) {
    	
        Integer bookingId;
        
        // 將 order_no 轉為 Integer 作為 bookingId
        try {
            bookingId = Integer.parseInt(data.getOrder_no());
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("無效的 order_no : " + data.getOrder_no());
        }

        // 根據 bookingId 查詢對應預約資料，若查無資料則丟出錯誤
        Booking booking = bookingRepository.findById(bookingId)
                .orElseThrow(() -> new RuntimeException("找不到 Booking ID :" + bookingId));

        // 建立 Payment 實體並填入資料
        Payment payment = new Payment();
        payment.setBooking(booking);
        payment.setOrderNo(data.getOrder_no());
        payment.setVnoticeNo(data.getVnotice_no());

        try {
            payment.setPayAmount(Integer.parseInt(data.getPay_amount()));
        } catch (NumberFormatException e) {
            throw new IllegalArgumentException("金額格式錯誤 :" + data.getPay_amount());
        }

        try {
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMddHHmmss");
            payment.setPayDatetime(LocalDateTime.parse(data.getPay_datetime(), formatter));
        } catch (Exception e) {
            throw new IllegalArgumentException("時間格式錯誤 :" + data.getPay_datetime());
        }

        payment.setPayStatus(data.getPay_status()); 		 // 付款狀態代碼，例如 "2" 表成功
        payment.setPayStatusDesc(data.getPay_status_desc()); // 付款狀態說明
        payment.setChannelId(data.getChannel_id()); 		 // 金流通道代碼
        payment.setChannelName(data.getChannel_name()); 	 // 金流通道名稱
        payment.setItemName(data.getItem_name()); 			 // 商品名稱
        
        // 如果付款狀態為成功，就把 booking.isPaid 設為 true
        if ("2".equals(data.getPay_status())) {
            booking.setIsPaid(true);
        }

        paymentRepository.save(payment);
        bookingRepository.save(booking);
    }
}
