package org.cook.extracter.controller.web;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.Category;
import org.cook.extracter.model.Document;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.interfaces.CategoryService;
import org.cook.extracter.service.interfaces.DocumentService;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;

import java.util.List;

@Controller
@RequestMapping("/documents")
@RequiredArgsConstructor
public class DocumentWebController {

    private final DocumentService documentService;
    private final CategoryService categoryService;

    @GetMapping
    public String documentsPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Document> documents = isAdmin ? documentService.getAllDocuments() : documentService.getAllDocumentsByUserId(userDetails.getId());

        model.addAttribute("documents", documents);
        model.addAttribute("isAdmin", isAdmin);

        return "documents/list";
    }

    @GetMapping("/upload")
    public String uploadPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        List<Category> categories = categoryService.getAllCategoriesByUserId(userDetails.getId());

        model.addAttribute("categories", categories);

        return "documents/upload";
    }

    @GetMapping("/preview")
    public String previewPage(@AuthenticationPrincipal CustomUserDetails userDetails, Model model) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        List<Category> categories = isAdmin ? categoryService.getAllCategories() : categoryService.getAllCategoriesByUserId(userDetails.getId());

        model.addAttribute("categories", categories);

        return "documents/preview";
    }

    @GetMapping("/{id}")
    public String documentDetailPage(@PathVariable Long id, Model model) {
        model.addAttribute("document", documentService.getDocumentById(id));

        return "documents/detail";
    }
}
