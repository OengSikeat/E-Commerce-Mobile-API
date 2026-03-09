package org.example.basiclogin.config;

import io.minio.MinioClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StringUtils;


@Configuration
public class MinioConfig {

    @Value("${minio.url:}")
    private String url;

    @Value("${minio.access-key:}")
    private String accessKey;

    @Value("${minio.secret-key:}")
    private String secretKey;

    @Bean
    public MinioClient minioClient() {
        if (!StringUtils.hasText(url)) {
            throw new IllegalStateException("MinIO url is not configured (minio.url)");
        }
        if (!StringUtils.hasText(accessKey)) {
            throw new IllegalStateException("MinIO access key is not configured (minio.access-key)");
        }
        if (!StringUtils.hasText(secretKey)) {
            throw new IllegalStateException("MinIO secret key is not configured (minio.secret-key)");
        }

        // MinIO expects an HTTP(S) endpoint. Example: http://localhost:9000
        String normalizedUrl = url.trim();

        return MinioClient.builder()
                .endpoint(normalizedUrl)
                .credentials(accessKey.trim(), secretKey)
                .build();
    }

}
