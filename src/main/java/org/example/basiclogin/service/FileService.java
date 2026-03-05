package org.example.basiclogin.service;

import org.example.basiclogin.model.Entity.FileMetadata;
import org.springframework.web.multipart.MultipartFile;
import java.io.InputStream;

public interface FileService {

    FileMetadata uploadFile(MultipartFile file);

    InputStream getFileByFileName(String fileName);
}
