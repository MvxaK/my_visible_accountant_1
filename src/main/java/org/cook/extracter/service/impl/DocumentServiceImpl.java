package org.cook.extracter.service.impl;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import org.cook.extracter.entity.DocumentEntity;
import org.cook.extracter.entity.UserEntity;
import org.cook.extracter.mapper.DocumentMapper;
import org.cook.extracter.model.Document;
import org.cook.extracter.repository.DocumentRepository;
import org.cook.extracter.repository.UserRepository;
import org.cook.extracter.service.interfaces.DocumentService;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class DocumentServiceImpl implements DocumentService {

    private final DocumentRepository documentRepository;
    private final DocumentMapper documentMapper;
    private final UserRepository userRepository;

    @Override
    public Document getDocumentById(Long id) {
        return documentRepository.findById(id)
                .map(documentMapper::toModel)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id -> " + id));
    }

    @Override
    public List<Document> getAllDocuments() {
        return documentRepository.findAll()
                .stream()
                .map(documentMapper::toModel)
                .toList();
    }

    @Override
    public List<Document> getAllDocumentsByUserId(Long userId) {
        return documentRepository.findAllByUserId(userId)
                .stream()
                .map(documentMapper::toModel)
                .toList();
    }

    @Override
    @Transactional
    public Document createDocument(Document documentToCreate, Long requestingUserId) {
        UserEntity userEntity = userRepository.findById(requestingUserId)
                .orElseThrow(() -> new EntityNotFoundException("User not found with id -> " + requestingUserId));

        DocumentEntity documentEntity = documentMapper.toEntity(documentToCreate);
        documentEntity.setUser(userEntity);

        return documentMapper.toModel(documentRepository.save(documentEntity));
    }

    @Override
    @Transactional
    public void updateDocument(Long id, Document documentToUpdate, Long requestingUserId, boolean isAdmin) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id -> " + id));

        if (!isAdmin && !documentEntity.getUser().getId().equals(requestingUserId)) {
            throw new AccessDeniedException("You don't have permission to update this document");
        }

        documentEntity.setName(documentToUpdate.getName());
        documentEntity.setFilePath(documentToUpdate.getFilePath());
        documentEntity.setDocumentType(documentToUpdate.getDocumentType());

        documentRepository.save(documentEntity);
    }

    @Override
    @Transactional
    public void deleteDocument(Long id, Long requestingUserId, boolean isAdmin) {
        DocumentEntity documentEntity = documentRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Document not found with id -> " + id));

        if (!isAdmin && !documentEntity.getUser().getId().equals(requestingUserId)) {
            throw new AccessDeniedException("You don't have permission to delete this document");
        }

        documentRepository.deleteById(id);
    }
}