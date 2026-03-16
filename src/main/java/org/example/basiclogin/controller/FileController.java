package org.example.basiclogin.controller;

import lombok.RequiredArgsConstructor;
import org.example.basiclogin.model.Entity.FileMetadata;
import org.example.basiclogin.service.FileService;
import org.example.basiclogin.utils.ApiResponse;
import org.example.basiclogin.utils.BaseResponse;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.MediaTypeFactory;
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
    public ResponseEntity<byte[]> previewFile(@PathVariable("file-name") String fileName) throws IOException {
        try (InputStream inputStream = fileService.getFileByFileName(fileName)) {
            byte[] data = inputStream.readAllBytes();

            MediaType mediaType = MediaTypeFactory.getMediaType(fileName)
                    .orElse(MediaType.APPLICATION_OCTET_STREAM);

            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(mediaType);
            headers.setContentDisposition(ContentDisposition.inline().filename(fileName).build());
            headers.setContentLength(data.length);

            return ResponseEntity.ok().headers(headers).body(data);
        }
    }

}
