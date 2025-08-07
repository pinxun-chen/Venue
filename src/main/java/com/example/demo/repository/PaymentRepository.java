package com.example.demo.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.Payment;


public interface PaymentRepository extends JpaRepository<Payment, Integer> {

	Optional<Payment> findByOrderNo(String orderNo);
}
