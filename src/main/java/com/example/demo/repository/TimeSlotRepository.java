package com.example.demo.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.TimeSlot;

public interface TimeSlotRepository extends JpaRepository<TimeSlot, Integer> {

}
