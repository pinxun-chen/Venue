package com.example.demo.model.entity;

import java.time.LocalDate;
import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Data;

@Entity
@Table( name = "bookings" )
@Data
public class Booking {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    @ManyToOne
    @JoinColumn(name = "venue_id", nullable = false)
    private Venue venue;

    @ManyToOne
    @JoinColumn(name = "time_slot_id", nullable = false)
    private TimeSlot timeSlot;

    @Column(name = "booking_date", nullable = false)
    private LocalDate bookingDate;

    @Column(name = "renter_name", nullable = false)
    private String renterName;

    @Column(name = "renter_email", nullable = false)
    private String renterEmail;

    @Column(name = "renter_phone", nullable = false)
    private String renterPhone;

    @Column(name = "is_paid", nullable = false)
    private Boolean isPaid = false;

    @Column(name = "created_at", nullable = false)
    private LocalDateTime createdAt = LocalDateTime.now();

}
