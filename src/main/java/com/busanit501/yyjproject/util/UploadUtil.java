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
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
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
import lombok.extern.log4j.Log4j2;

@Component
@Log4j2
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
            String uuid = UUID.randomUUID().toString();

            boolean isImage = false;
            try {
                String encodedFileName = java.net.URLEncoder.encode(originalName, "UTF-8").replaceAll("\\+", "%20");
                String objectKey = uuid + "_" + encodedFileName;

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
                log.error("파일 업로드 중 오류 발생: " + originalName, e);
            }

            resultList.add(UploadResultDTO.builder()
                    .uuid(uuid)
                    .fileName(originalName) // 원본 파일명 저장
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

    public void deleteFileFromMinio(String objectKey) {
        try {
            // 원본 파일 삭제
            DeleteObjectRequest deleteObjectRequest = DeleteObjectRequest.builder()
                    .bucket(minioBucketName)
                    .key(objectKey)
                    .build();
            s3Client.deleteObject(deleteObjectRequest);
            log.info("Deleted original file from MinIO: " + objectKey);

            // 썸네일 파일 삭제 (만약 썸네일이 있다면)
            // 이 부분은 objectKey가 썸네일인 경우 원본을, 원본인 경우 썸네일을 삭제하도록 로직이 복잡하게 되어 있습니다.
            // MinIO에 저장된 objectKey는 UUID_EncodedFileName 형태이므로,
            // 썸네일은 s_UUID_EncodedFileName 형태입니다.
            // 따라서 objectKey가 's_'로 시작하면 썸네일이고, 그렇지 않으면 원본입니다.
            if (objectKey.startsWith("s_")) { // 썸네일 파일 삭제 요청이 들어온 경우
                // 원본 파일의 objectKey를 추정하여 삭제 시도
                String originalObjectKey = objectKey.substring(2); // 's_' 제거
                DeleteObjectRequest deleteOriginalRequest = DeleteObjectRequest.builder()
                        .bucket(minioBucketName)
                        .key(originalObjectKey)
                        .build();
                s3Client.deleteObject(deleteOriginalRequest);
                log.info("Deleted associated original file from MinIO: " + originalObjectKey);
            } else { // 원본 파일 삭제 요청이 들어온 경우
                // 썸네일 파일의 objectKey를 추정하여 삭제 시도
                String thumbnailObjectKey = "s_" + objectKey;
                DeleteObjectRequest deleteThumbnailRequest = DeleteObjectRequest.builder()
                        .bucket(minioBucketName)
                        .key(thumbnailObjectKey)
                        .build();
                s3Client.deleteObject(deleteThumbnailRequest);
                log.info("Deleted associated thumbnail file from MinIO: " + thumbnailObjectKey);
            }
        } catch (Exception e) {
            log.error("Error deleting file from MinIO: " + objectKey, e);
        }
    }
}