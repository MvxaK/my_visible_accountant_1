package org.cook.extracter.controller.api;

import lombok.RequiredArgsConstructor;
import org.cook.extracter.model.Document;
import org.cook.extracter.model.ExtractedReceipt;
import org.cook.extracter.security.details.CustomUserDetails;
import org.cook.extracter.service.ReceiptService;
import org.cook.extracter.service.interfaces.DocumentService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/documents")
@RequiredArgsConstructor
public class DocumentApiController {

    private final DocumentService documentService;
    private final ReceiptService receiptService;

    @GetMapping
    public ResponseEntity<List<Document>> getAllDocuments() {
        return ResponseEntity.ok(documentService.getAllDocuments());
    }

    @GetMapping("/{id}")
    public ResponseEntity<Document> getDocumentById(@PathVariable Long id) {
        return ResponseEntity.ok(documentService.getDocumentById(id));
    }

    @GetMapping("/my")
    public ResponseEntity<List<Document>> getMyDocuments(@AuthenticationPrincipal CustomUserDetails userDetails) {
        List<Document> documents = documentService.getAllDocumentsByUserId(userDetails.getId());

        return ResponseEntity.ok(documents);
    }

    @PostMapping("/upload")
    public ResponseEntity<?> uploadPreview(@RequestParam("file") MultipartFile file, @AuthenticationPrincipal CustomUserDetails userDetails) {
        try {
            ExtractedReceipt extracted = receiptService.processReceipt(file);

            return ResponseEntity.ok(Map.of(
                    "extractedAmount",   extracted.getAmount(),
                    "extractedCurrency", extracted.getCurrency(),
                    "fileName",          file.getOriginalFilename() != null ? file.getOriginalFilename() : "document",
                    "fileSize",          file.getSize(),
                    "rawText",           extracted.getRawText() != null ? extracted.getRawText() : ""
            ));
        } catch (Exception e) {
            return ResponseEntity.badRequest()
                    .body(Map.of("error", "Failed to process file: " + e.getMessage()));
        }
    }

    @PostMapping("/save")
    public ResponseEntity<Document> saveDocument(@RequestBody Document documentToSave, @AuthenticationPrincipal CustomUserDetails userDetails) {
        Document saved = documentService.createDocument(documentToSave, userDetails.getId());

        return ResponseEntity.ok(saved);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Void> updateDocument(@PathVariable Long id, @RequestBody Document document, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        documentService.updateDocument(id, document, userDetails.getId(), isAdmin);

        return ResponseEntity.noContent()
                .build();
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteDocument(@PathVariable Long id, @AuthenticationPrincipal CustomUserDetails userDetails) {
        boolean isAdmin = userDetails.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        documentService.deleteDocument(id, userDetails.getId(), isAdmin);

        return ResponseEntity.noContent()
                .build();
    }
}