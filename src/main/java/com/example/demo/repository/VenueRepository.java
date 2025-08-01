package com.example.demo.repository;

import com.example.demo.model.entity.Venue;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

public interface VenueRepository extends JpaRepository<Venue, Integer> {

	// 模糊比對名稱收尋場地
	List<Venue> findByNameContainingIgnoreCase(String name);


}
