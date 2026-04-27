package org.cook.extracter.service.interfaces;

import org.cook.extracter.model.Category;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface CategoryService {

    Category getCategoryById(Long id);
    List<Category> getAllCategories();
    List<Category> getAllCategoriesByUserId(Long userId);
    Category createCategory(Category categoryToCreate, Long requestingUserId);
    Category updateCategory(Long id, Category categoryToUpdate, Long requestingUserId, boolean isAdmin);
    void deleteCategory(Long id, Long requestingUserId, boolean isAdmin);

}
