package org.cook.extracter.service.interfaces;

import org.cook.extracter.model.Document;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public interface DocumentService {

    Document getDocumentById(Long id);
    List<Document> getAllDocuments();
    List<Document> getAllDocumentsByUserId(Long userId);
    Document createDocument(Document documentToCreate, Long requestingUserId);
    void updateDocument(Long id, Document documentToUpdate, Long requestingUserId, boolean isAdmin);
    void deleteDocument(Long id, Long requestingUserId, boolean isAdmin);

}
