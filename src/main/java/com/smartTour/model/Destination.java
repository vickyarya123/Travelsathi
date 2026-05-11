package com.smartTour.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
//import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@NoArgsConstructor
@Getter
@Setter
@AllArgsConstructor
//@Builder
public class Destination {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private int id;

	@Column(length = 300)
	private String title;

	@Column(length = 2000)
	private String description;

	private String category; // temple, restaurant, park, etc.

	private String state;

	private String city;

	private String location;

	private Double price;

	private int duration;
	
	private String image;

//	private String address;
//	private String country;
 	private Boolean isActive;


}
