package com.smartTour.service;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.web.multipart.MultipartFile;

import com.smartTour.model.Destination;

public interface DestinationService {

	public Destination saveDestination(Destination destination);

	public List<Destination> getAllDestination();

	public Boolean deleteDestination(Integer id);

	public Destination getDestinationById(Integer id);

	public List<Destination> getAllActiveDestination(String category);

	public Destination updateDestination(Destination destination, MultipartFile file);

	public List<Destination> searchDestination(String ch);
	
	public List<String> getAllStates();

	public List<Integer> getAllDurations();

	public Page<Destination> getAllActiveDestinationPagination(Integer pageNo, Integer pageSize, String category);

	public Page<Destination> searchDestinationPagination(Integer pageNo, Integer pageSize, String ch);

	public Page<Destination> getAllDestinationPagination(Integer pageNo, Integer pageSize);

	public Page<Destination> searchActiveDestinationPagination(Integer pageNo, Integer pageSize, String category,
			String ch);
	Page<Destination> filterDestination(Integer pageNo, Integer pageSize,
	        List<String> category,
	        List<String> state,
	        List<Integer> duration,
	        String ch);

}
