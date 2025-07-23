package com.busanit501.yyjproject.util;

import net.coobird.thumbnailator.Thumbnailator;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import com.busanit501.yyjproject.dto.UploadResultDTO;
import software.amazon.awssdk.auth.credentials.AwsBasicCredentials;
import software.amazon.awssdk.auth.credentials.StaticCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.GetObjectRequest;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;
import software.amazon.awssdk.services.s3.model.PutObjectRequest;
import software.amazon.awssdk.core.ResponseInputStream;

import jakarta.annotation.PostConstruct;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.net.URI;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Component
public class UploadUtil {

    @Value("${minio.url}")
    private String minioUrl;

    @Value("${minio.accessKey}")
    private String minioAccessKey;

    @Value("${minio.secretKey}")
    private String minioSecretKey;

    @Value("${minio.bucketName}")
    private String minioBucketName;

    private S3Client s3Client;

    @PostConstruct
    public void init() {
        this.s3Client = S3Client.builder()
                .endpointOverride(URI.create(minioUrl))
                .credentialsProvider(StaticCredentialsProvider.create(
                        AwsBasicCredentials.create(minioAccessKey, minioSecretKey)))
                .region(Region.US_EAST_1) // MinIO는 리전 개념이 없지만, S3 SDK 사용을 위해 아무 리전이나 설정
                // 이 변경이 문제를 해결한 이유:
                // MinIO와 같은 S3 호환 오브젝트 스토리지 시스템은 일반적으로 Path Style URL을 사용합니다.
                // 예를 들어, http://your-minio-server:9000/your-bucket/your-object.jpg 와 같은 형식입니다.
                //
                // 반면, AWS S3 SDK는 기본적으로 Virtual Hosted Style URL을 선호합니다.
                // 예를 들어, http://your-bucket.s3.amazonaws.com/your-object.jpg 또는 MinIO의 경우
                // http://your-bucket.your-minio-server:9000/your-object.jpg 와 같은 형식입니다.
                //
                // 이 두 가지 URL 스타일 간의 불일치 때문에 S3 SDK가 MinIO에 요청을 보낼 때 MinIO 서버가 해당 요청을 "유효하지 않은 버킷"으로 인식하여
                // The specified bucket is not valid 오류를 반환했던 것입니다.
                //
                // .forcePathStyle(true) 옵션을 추가함으로써, AWS S3 SDK가 MinIO와 통신할 때 Path Style URL을 사용하도록 강제하여 이 호환성 문제를 해결했습니다.
                .forcePathStyle(true) // MinIO와 같은 S3 호환 스토리지 사용 시 필요
                .build();
    }

    public List<UploadResultDTO> uploadFiles(List<MultipartFile> files) {

        List<UploadResultDTO> resultList = new ArrayList<>();

        for (MultipartFile multipartFile : files) {

            if (multipartFile.isEmpty()) {
                continue;
            }

            String originalName = multipartFile.getOriginalFilename();
            String safeFileName = originalName.replaceAll("[^a-zA-Z0-9._-]", "");
            String uuid = UUID.randomUUID().toString();
            String objectKey = uuid + "_" + safeFileName;

            boolean isImage = false;
            try {
                // 원본 파일 업로드
                PutObjectRequest putObjectRequest = PutObjectRequest.builder()
                        .bucket(minioBucketName)
                        .key(objectKey)
                        .contentType(multipartFile.getContentType())
                        .build();
                s3Client.putObject(putObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                        multipartFile.getInputStream(), multipartFile.getSize()));

                // 이미지 파일 여부 확인 및 썸네일 생성 후 업로드
                if (multipartFile.getContentType() != null && multipartFile.getContentType().startsWith("image")) {
                    isImage = true;
                    String thumbnailObjectKey = "s_" + objectKey;

                    ByteArrayOutputStream thumbnailOutputStream = new ByteArrayOutputStream();
                    Thumbnailator.createThumbnail(multipartFile.getInputStream(), thumbnailOutputStream, 200, 200);
                    ByteArrayInputStream thumbnailInputStream = new ByteArrayInputStream(thumbnailOutputStream.toByteArray());

                    PutObjectRequest thumbnailPutObjectRequest = PutObjectRequest.builder()
                            .bucket(minioBucketName)
                            .key(thumbnailObjectKey)
                            .contentType(multipartFile.getContentType())
                            .build();
                    s3Client.putObject(thumbnailPutObjectRequest, software.amazon.awssdk.core.sync.RequestBody.fromInputStream(
                            thumbnailInputStream, thumbnailOutputStream.size()));
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

            resultList.add(UploadResultDTO.builder()
                    .uuid(uuid)
                    .fileName(originalName)
                    .img(isImage)
                    .build());
        }
        return resultList;
    }

    public ResponseInputStream<GetObjectResponse> getFileFromMinio(String objectKey) {
        GetObjectRequest getObjectRequest = GetObjectRequest.builder()
                .bucket(minioBucketName)
                .key(objectKey)
                .build();
        return s3Client.getObject(getObjectRequest);
    }
}
