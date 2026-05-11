package com.smartTour.service.impl;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;
import org.springframework.web.multipart.MultipartFile;

import com.smartTour.model.Destination;
import com.smartTour.repository.DestinationRepository;
import com.smartTour.service.DestinationService;

@Service
public class DestinationServiceImpl implements DestinationService {

	@Autowired
	private DestinationRepository destinationRepository;

	@Override
	public Destination saveDestination(Destination destination) {
		return destinationRepository.save(destination);
	}

	@Override
	public List<Destination> getAllDestination() {
		return destinationRepository.findAll();
	}

	@Override
	public Boolean deleteDestination(Integer id) {
		Destination destination = destinationRepository.findById(id).orElse(null);

		if (!ObjectUtils.isEmpty(destination)) {
			destinationRepository.delete(destination);
			return true;
		}
		return false;
	}

	@Override
	public Destination getDestinationById(Integer id) {
		Destination destination = destinationRepository.findById(id).orElse(null);
		return destination;
	}
	
	
	@Override
	public Destination updateDestination(Destination destination, MultipartFile image) {

	    Destination dbDestination = getDestinationById(destination.getId());

	    String uploadDir = "C:/springbootproject/SmartTour/images/destination/";

	    String imageName;

	    try {

	        // If new image uploaded
	        if (image != null && !image.isEmpty()) {

	            //  Unique image name (important)
	            imageName = UUID.randomUUID() + "_" + image.getOriginalFilename();

	            //  Save image in external folder
	            Path path = Paths.get(uploadDir + imageName);
	            Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

	        } else {
	            //  Keep old image
	            imageName = dbDestination.getImage();
	        }

	        //  Update fields
	        dbDestination.setTitle(destination.getTitle());
	        dbDestination.setDescription(destination.getDescription());
	        dbDestination.setCategory(destination.getCategory());
	        dbDestination.setState(destination.getState());
	        dbDestination.setCity(destination.getCity());
	        dbDestination.setLocation(destination.getLocation());
	        dbDestination.setPrice(destination.getPrice());
	        dbDestination.setDuration(destination.getDuration());
	        dbDestination.setIsActive(destination.getIsActive());
	        dbDestination.setImage(imageName);

	        return destinationRepository.save(dbDestination);

	    } catch (Exception e) {
	        e.printStackTrace();
	    }

	    return null;
	}
	
	@Override
	public List<Destination> searchDestination(String ch) {
		return destinationRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrStateContainingIgnoreCaseOrCityContainingIgnoreCase(ch, ch,ch ,ch);

	}

	@Override
	public Page<Destination> getAllActiveDestinationPagination(Integer pageNo, Integer pageSize, String category) {

		Pageable pageable = PageRequest.of(pageNo, pageSize);
		Page<Destination> pageDestination = null;

		if (ObjectUtils.isEmpty(category)) {
			pageDestination = destinationRepository.findByIsActiveTrue(pageable);
		} else {
			pageDestination= destinationRepository.findByCategory(pageable, category);
		}
		return pageDestination;
	}

	@Override
	public Page<Destination> searchDestinationPagination(Integer pageNo, Integer pageSize, String ch) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return destinationRepository.findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch, ch, pageable);

	}

	@Override
	public Page<Destination> getAllDestinationPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return destinationRepository.findAll(pageable);

	}

	@Override
	public Page<Destination> searchActiveDestinationPagination(Integer pageNo, Integer pageSize, String category,
			String ch) {
		Page<Destination> pageDestination = null;
		Pageable pageable = PageRequest.of(pageNo, pageSize);

		pageDestination = destinationRepository.findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(ch,
				ch, pageable);
		return pageDestination;
	}

	@Override
	public List<Destination> getAllActiveDestination(String category) {
		List<Destination> destination = null;
		if (ObjectUtils.isEmpty(category)) {
			destination = destinationRepository.findByIsActiveTrue();
		} else {
			destination = destinationRepository.findByCategory(category);

		}
		return destination;
	}
	
	public List<String> getAllStates() {
	    return destinationRepository.findDistinctStates();
	}

	public List<Integer> getAllDurations() {
	    return destinationRepository.findDistinctDurations();
	}

	@Override
	public Page<Destination> filterDestination(Integer pageNo, Integer pageSize,
	        List<String> category,
	        List<String> state,
	        List<Integer> duration,
	        String ch) {

	    Pageable pageable = PageRequest.of(pageNo, pageSize);

	    boolean hasCategory = category != null && !category.isEmpty();
	    boolean hasState = state != null && !state.isEmpty();
	    boolean hasDuration = duration != null && !duration.isEmpty();
	    boolean hasSearch = ch != null && !ch.trim().isEmpty();

	    if (hasSearch) {
	        return destinationRepository.searchWithFilter(ch, category, state, duration, pageable);
	    }

	    return destinationRepository.filterData(category, state, duration, pageable);
	}
	
}
