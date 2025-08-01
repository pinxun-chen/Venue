package com.example.demo.model.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.Data;

@Entity
@Table(
		name = "venue_time",
		uniqueConstraints = @UniqueConstraint(columnNames = {"venue_id", "time_slot_id"})
		)
@Data
public class VenueTime {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Integer id;
	
	@ManyToOne
	@JoinColumn(name = "venue_id", nullable = false)
	private Venue venue;
	
	@ManyToOne
	@JoinColumn(name = "time_slot_id", nullable = false)
	private TimeSlot timeSlot;

}
