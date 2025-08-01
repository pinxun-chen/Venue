package com.example.demo.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.demo.model.entity.VenueTime;

public interface VenueTimeRepository extends JpaRepository<VenueTime, Integer> {

	// 根據場地 id 收尋可用時段
	List<VenueTime> findByVenueId(Integer venueId);
}
