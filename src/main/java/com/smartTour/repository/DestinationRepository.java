package com.smartTour.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.smartTour.model.Destination;

public interface DestinationRepository extends JpaRepository<Destination, Integer> {

	List<Destination> findByIsActiveTrue();

	Page<Destination> findByIsActiveTrue(Pageable pageable);

	List<Destination> findByCategory(String category);

	Page<Destination> findByCategory(Pageable pageable, String category);

	List<Destination> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCaseOrStateContainingIgnoreCaseOrCityContainingIgnoreCase(String ch, String ch2, String ch3,String ch4);

	Page<Destination> findByTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch, String ch2,
			Pageable pageable);

	Page<Destination> findByisActiveTrueAndTitleContainingIgnoreCaseOrCategoryContainingIgnoreCase(String ch,
			String ch2, Pageable pageable);
	
	 @Query(value = "SELECT DISTINCT state FROM destination", nativeQuery = true)
	    List<String> findDistinctStates();

	    @Query(value = "SELECT DISTINCT duration FROM destination", nativeQuery = true)
	    List<Integer> findDistinctDurations();
	    
//	    @Query(value = """
//	    		SELECT * FROM destination d
//	    		WHERE d.is_active = 1
//	    		AND (:category IS NULL OR d.category IN (:category))
//	    		AND (:state IS NULL OR d.state IN (:state))
//	    		AND (:duration IS NULL OR d.duration IN (:duration))
//	    		""",
//	    		nativeQuery = true)
//	    		Page<Destination> filterData(List<String> category,
//	    		                             List<String> state,
//	    		                             List<Integer> duration,
//	    		                             Pageable pageable);
	    @Query("""
	    		SELECT d FROM Destination d
	    		WHERE d.isActive = true
	    		AND (:category IS NULL OR d.category IN :category)
	    		AND (:state IS NULL OR d.state IN :state)
	    		AND (:duration IS NULL OR d.duration IN :duration)
	    		""")
	    		Page<Destination> filterData(
	    		    @Param("category") List<String> category,
	    		    @Param("state") List<String> state,
	    		    @Param("duration") List<Integer> duration,
	    		    Pageable pageable);
	    
	    @Query(value = """
	    		SELECT * FROM destination d
	    		WHERE d.is_active = 1
	    		AND (d.title LIKE %:ch% OR d.category LIKE %:ch%)
	    		AND (:category IS NULL OR d.category IN (:category))
	    		AND (:state IS NULL OR d.state IN (:state))
	    		AND (:duration IS NULL OR d.duration IN (:duration))
	    		""",
	    		nativeQuery = true)
	    		Page<Destination> searchWithFilter(String ch,
	    		                                   List<String> category,
	    		                                   List<String> state,
	    		                                   List<Integer> duration,
	    		                                   Pageable pageable);

}
