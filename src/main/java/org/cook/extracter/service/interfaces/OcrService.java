package org.cook.extracter.service.interfaces;

import org.cook.extracter.model.Document;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

@Service
public interface OcrService {

    Document processFile(MultipartFile fil, Long userId);

}
