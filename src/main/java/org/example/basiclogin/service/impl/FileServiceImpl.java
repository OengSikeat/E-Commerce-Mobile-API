package org.example.basiclogin.service.impl;

import io.minio.*;
import io.minio.errors.ErrorResponseException;
import io.minio.errors.InsufficientDataException;
import io.minio.errors.InternalException;
import io.minio.errors.InvalidResponseException;
import io.minio.errors.ServerException;
import io.minio.errors.XmlParserException;
import lombok.RequiredArgsConstructor;
import org.example.basiclogin.exception.BadRequestException;
import org.example.basiclogin.exception.NotFoundException;
import org.example.basiclogin.model.Entity.FileMetadata;
import org.example.basiclogin.service.FileService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.support.ServletUriComponentsBuilder;

import java.io.IOException;
import java.io.InputStream;
import java.security.NoSuchAlgorithmException;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class FileServiceImpl implements FileService {

    @Value("${minio.bucket-name:}")
    private String bucketName;

    private final MinioClient minioClient;

    @Override
    public FileMetadata uploadFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new BadRequestException("File is required");
        }
        if (!StringUtils.hasText(bucketName)) {
            throw new BadRequestException("MinIO bucket name is not configured (minio.bucket-name)");
        }

        try {
            boolean bucketExists = minioClient.bucketExists(
                    BucketExistsArgs.builder().bucket(bucketName).build()
            );

            if (!bucketExists) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucketName).build());
            }

            String originalName = file.getOriginalFilename();
            String ext = StringUtils.getFilenameExtension(originalName);
            String fileName = UUID.randomUUID() + (StringUtils.hasText(ext) ? ("." + ext) : "");

            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .contentType(file.getContentType())
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .build()
            );

            String fileUrl = ServletUriComponentsBuilder.fromCurrentContextPath()
                    .path("/api/v1/files/preview-file/")
                    .path(fileName)
                    .toUriString();

            return FileMetadata.builder()
                    .fileName(fileName)
                    .fileUrl(fileUrl)
                    .fileType(file.getContentType())
                    .fileSize(file.getSize())
                    .build();

        } catch (ErrorResponseException e) {
            // SignatureDoesNotMatch, AccessDenied, NoSuchBucket, etc.
            String code = e.errorResponse() != null ? e.errorResponse().code() : null;
            if ("NoSuchBucket".equalsIgnoreCase(code)) {
                throw new NotFoundException("MinIO bucket not found: " + bucketName);
            }
            throw new BadRequestException("MinIO error: " + (code != null ? code : e.getMessage()));
        } catch (IOException e) {
            throw new BadRequestException("Failed to read uploaded file");
        } catch (InsufficientDataException | java.security.InvalidKeyException | InvalidResponseException | NoSuchAlgorithmException |
                 ServerException | InternalException | XmlParserException e) {
            throw new BadRequestException("File upload failed: " + e.getMessage());
        } catch (Exception e) {
            // Some IDEs mis-infer multi-catch types; keep this super safe.
            throw new BadRequestException("Unexpected upload failure: " + String.valueOf(e));
        }
    }

    @Override
    public InputStream getFileByFileName(String fileName) {
        if (!StringUtils.hasText(fileName)) {
            throw new BadRequestException("fileName is required");
        }
        if (!StringUtils.hasText(bucketName)) {
            throw new BadRequestException("MinIO bucket name is not configured (minio.bucket-name)");
        }

        try {
            return minioClient.getObject(
                    GetObjectArgs.builder()
                            .bucket(bucketName)
                            .object(fileName)
                            .build()
            );
        } catch (ErrorResponseException e) {
            String code = e.errorResponse() != null ? e.errorResponse().code() : null;
            if ("NoSuchKey".equalsIgnoreCase(code) || "NoSuchObject".equalsIgnoreCase(code)) {
                throw new NotFoundException("File not found");
            }
            throw new BadRequestException("MinIO error: " + (code != null ? code : e.getMessage()));
        } catch (Exception e) {
            throw new BadRequestException("Failed to retrieve file: " + e.getMessage());
        }
    }

}
