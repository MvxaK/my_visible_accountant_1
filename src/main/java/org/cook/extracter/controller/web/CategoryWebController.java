package org.cook.extracter.controller.web;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.Category;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.CategoryService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/categories")
@RequiredArgsConstructor
public class CategoryWebController {

    private final CategoryService categoryService;

    @GetMapping
    public String categoriesPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Category> categories = isAdmin ? categoryService.getAllCategories() : categoryService.getAllCategoriesByUserId(userDetails.getId());

        model.addAttribute("categories", categories);
        model.addAttribute("isAdmin", isAdmin);

        return "categories/list";
    }

    @GetMapping("/{id}/edit")
    public String editCategoryPage(@PathVariable Long id, Model model) {
        model.addAttribute("category", categoryService.getCategoryById(id));

        return "categories/edit";
    }

}