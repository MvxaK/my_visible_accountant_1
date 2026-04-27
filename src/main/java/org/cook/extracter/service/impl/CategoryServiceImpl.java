package org.cook.extracter.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cook.extracter.entity.CategoryEntity;
import org.cook.extracter.entity.UserEntity;
import org.cook.extracter.mapper.CategoryMapper;
import org.cook.extracter.model.Category;
import org.cook.extracter.repository.CategoryRepository;
import org.cook.extracter.repository.UserRepository;
import org.cook.extracter.service.interfaces.CategoryService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CategoryServiceImpl implements CategoryService {

    private final CategoryMapper categoryMapper;
    private final CategoryRepository categoryRepository;
    private final UserRepository userRepository;

    @Override
    public Category getCategoryById(Long id) {
        return categoryRepository.findById(id)
                .map(categoryMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("There is no category with id -> " + id));
    }

    @Override
    public List<Category> getAllCategories() {
        return categoryRepository.findAll()
                .stream()
                .map(categoryMapper::toModel)
                .toList();
    }

    @Override
    public List<Category> getAllCategoriesByUserId(Long userId) {
        return categoryRepository.findAllByUserId(userId)
                .stream()
                .map(categoryMapper::toModel)
                .toList();
    }

    @Override
    @Transactional
    public Category createCategory(Category categoryToCreate, Long requestingUserId) {
        UserEntity userEntity = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id -> " + requestingUserId));

        CategoryEntity categoryEntity = new CategoryEntity();
        categoryEntity.setName(categoryToCreate.getName());
        categoryEntity.setType(categoryToCreate.getType());
        categoryEntity.setDescription(categoryToCreate.getDescription());
        categoryEntity.setUser(userEntity);

        return categoryMapper.toModel(categoryRepository.save(categoryEntity));
    }

    @Override
    @Transactional
    public Category updateCategory(Long id, Category categoryToUpdate, Long requestingUserId, boolean isAdmin) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no category with id -> " + id));

        if (!isAdmin && !categoryEntity.getUser().getId().equals(requestingUserId)) {
            throw new AccessDeniedException("You don't have permission to update this category");
        }

        categoryEntity.setName(categoryToUpdate.getName());
        categoryEntity.setDescription(categoryToUpdate.getDescription());
        categoryEntity.setType(categoryToUpdate.getType());

        return categoryMapper.toModel(categoryRepository.save(categoryEntity));
    }

    @Override
    @Transactional
    public void deleteCategory(Long id, Long requestingUserId, boolean isAdmin) {
        CategoryEntity categoryEntity = categoryRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("There is no category with id -> " + id));

        if (!isAdmin && !categoryEntity.getUser().getId().equals(requestingUserId)) {
            throw new AccessDeniedException("You don't have permission to delete this category");
        }

        categoryRepository.deleteById(id);
    }
}