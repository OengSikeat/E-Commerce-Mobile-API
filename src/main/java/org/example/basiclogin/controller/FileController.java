package org.example.basiclogin.controller;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.model.Entity.FileMetadata;
import org.example.basiclogin.service.FileService;
import org.example.basiclogin.utils.ApiResponse;
import org.example.basiclogin.utils.BaseResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
public class FileController extends BaseResponse {

    private final FileService fileService;

    @PostMapping(value = "/upload-file", consumes = "multipart/form-data")
    public ResponseEntity<ApiResponse<FileMetadata>> uploadFile(@RequestParam MultipartFile file) {
        FileMetadata fileMetadata = fileService.uploadFile(file);
        return responseEntity(true, "Upload file successfully!", HttpStatus.CREATED, fileMetadata);
    }

    @GetMapping("/preview-file/{file-name}")
    public ResponseEntity<ApiResponse<byte[]>> getFileByFileName(@PathVariable("file-name") String fileName) throws IOException {
        InputStream inputStream = fileService.getFileByFileName(fileName);
        byte[] data = inputStream.readAllBytes();
        return responseEntity(true, "File retrieved", HttpStatus.OK, data);
    }

}
