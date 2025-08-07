package com.example.demo.model.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table(name = "payments")
@Data
public class Payment {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "booking_id")
    private Booking booking;

    private String orderNo;
    
    private String vnoticeNo;
    
    private Integer payAmount;

    private LocalDateTime payDatetime;
    private String payStatus;
    
    private String payStatusDesc;
    
    private String channelId;
    
    private String channelName;
    
    private String itemName;

    private LocalDateTime createdAt = LocalDateTime.now();
}
