package com.devsuperior.dscatalog.services;

import java.util.List;
import java.util.stream.Collectors;

import javax.persistence.EntityNotFoundException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.dao.EmptyResultDataAccessException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.devsuperior.dscatalog.dtos.CategoryDTO;
import com.devsuperior.dscatalog.entities.Category;
import com.devsuperior.dscatalog.repositories.CategoryRepository;
import com.devsuperior.dscatalog.services.exceptions.DatabaseException;
import com.devsuperior.dscatalog.services.exceptions.ResourceNotFoundException;

@Service
public class CategoryService {

	@Autowired
	private CategoryRepository repository;

	@Transactional(readOnly = true)
	public List<CategoryDTO> findAll() {
		List<Category> list = repository.findAll();
		return list.stream().map(entity -> new CategoryDTO(entity)).collect(Collectors.toList());
	}
	
	@Transactional(readOnly = true)
	public Page<CategoryDTO> findAllPaged(PageRequest pageRequest) {
		Page<Category> page = repository.findAll(pageRequest);
		return page.map(entity -> new CategoryDTO(entity));
	}

	@Transactional(readOnly = true)
	public CategoryDTO findById(Long id) {
		return new CategoryDTO(
				repository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Entity not found")));
	}

	@Transactional
	public CategoryDTO insert(CategoryDTO dto) {
		Category entity = new Category();
		entity.setName(dto.getName());
		entity = repository.save(entity);
		return new CategoryDTO(entity);
	}

	@Transactional
	public CategoryDTO update(Long id, CategoryDTO dto) {
		try {
			Category entity = repository.getOne(id);
			entity.setName(dto.getName());
			entity = repository.save(entity);
			return new CategoryDTO(entity);
		} catch (EntityNotFoundException e) {
			throw new ResourceNotFoundException("ID not found " + id);
		}
	}

	public void delete(Long id) {
		try {
			repository.deleteById(id);
		}catch (EmptyResultDataAccessException e) { // Em caso de deletar um ID que não existe
			throw new ResourceNotFoundException("ID not found " + id);
		}catch (DataIntegrityViolationException e) {  // Em caso de delete uma CATEGORIA que tenha PRODUTOS depentes
			throw new DatabaseException("Integrity violation!");
		}
	}
}