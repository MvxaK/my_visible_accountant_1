package org.cook.extracter.controller.api;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.Category;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.CategoryService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/category")
@RequiredArgsConstructor
public class CategoryApiController {

    private final CategoryService categoryService;

    @GetMapping
    public ResponseEntity<List<Category>> getAllCategories() {
        List<Category> categories = categoryService.getAllCategories();

        return ResponseEntity.ok(categories);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Category> getCategoryById(@PathVariable Long id) {
        Category category = categoryService.getCategoryById(id);

        return ResponseEntity.ok(category);
    }

    @GetMapping("/my")
    public ResponseEntity<List<Category>> getMyCategories(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Category> categories = categoryService.getAllCategoriesByUserId(userDetails.getId());

        return ResponseEntity.ok(categories);
    }

    @PostMapping
    public ResponseEntity<Category> createCategory(@RequestBody Category category, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Category createdCategory = categoryService.createCategory(category, userDetails.getId());

        return ResponseEntity.ok(createdCategory);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Category> updateCategory(@PathVariable Long id, @RequestBody Category category, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        Category updatedCategory = categoryService.updateCategory(id, category, userDetails.getId(), isAdmin);

        return ResponseEntity.ok(updatedCategory);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteCategory(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        categoryService.deleteCategory(id, userDetails.getId(), isAdmin);

        return ResponseEntity.noContent()
                .build();
    }

}