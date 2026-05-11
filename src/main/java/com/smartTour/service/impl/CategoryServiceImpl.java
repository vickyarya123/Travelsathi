package com.smartTour.service.impl;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.util.ObjectUtils;

import com.smartTour.model.Category;
import com.smartTour.repository.CategoryRepository;
import com.smartTour.service.CategoryService;

@Service
public class CategoryServiceImpl implements CategoryService {

	@Autowired
	private CategoryRepository categoryRepository;

	@Override
	public Category saveCategory(Category category) {

		return categoryRepository.save(category);
	}

	@Override
	public List<Category> getAllCategory() {

		return categoryRepository.findAll();
	}

	@Override
	public Boolean existCategory(String name) {

		return categoryRepository.existsByName(name);
	}

	@Override
	public Boolean deleteCategory(int id) {
		Category category = categoryRepository.findById(id).orElse(null);
		if (!ObjectUtils.isEmpty(category)) {
			categoryRepository.delete(category);
			return true;
		}
		return false;
	}

	@Override
	public Category getCategoryById(int id) {

		return categoryRepository.findById(id).orElse(null);
	}

	@Override
	public List<Category> getAllActiveCategory() {
		List<Category> categories = categoryRepository.findAllByIsActiveTrue();
		return categories;
	}

	@Override
	public Page<Category> getAllCategorPagination(Integer pageNo, Integer pageSize) {
		Pageable pageable = PageRequest.of(pageNo, pageSize);
		return categoryRepository.findAll(pageable);
	}

}
