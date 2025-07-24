package com.busanit501.yyjproject.controller;

import com.busanit501.yyjproject.util.UploadUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import software.amazon.awssdk.core.ResponseInputStream;
import software.amazon.awssdk.services.s3.model.GetObjectResponse;

import java.io.IOException;
import java.net.URLEncoder;

@RestController
@RequiredArgsConstructor
@Log4j2
public class FileController {

    private final UploadUtil uploadUtil;

    /*
    // MinIO에서 파일 가져오기
    // 문제점: 이전에는 Content-Type이 MediaType.IMAGE_JPEG로 고정되어 있어
    // JPEG가 아닌 다른 이미지 형식(PNG, GIF 등)이나 다른 파일 형식(PDF 등)을 요청할 경우
    // 브라우저에서 올바르게 표시되지 않거나 다운로드되지 않는 문제가 있었음.
    // 해결: MinIO에서 가져온 파일의 실제 Content-Type을 동적으로 설정하도록 수정.
     */


    @GetMapping("/view/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable String fileName) {
        try {
            // @PathVariable로 받은 fileName은 디코딩된 상태이므로, MinIO 요청을 위해 다시 URL 인코딩
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            ResponseInputStream<GetObjectResponse> is = uploadUtil.getFileFromMinio(encodedFileName);
            byte[] data = is.readAllBytes();

            return ResponseEntity.ok()
                    .contentType(MediaType.parseMediaType(is.response().contentType())) // 동적으로 Content-Type 설정
                    .body(data);
        } catch (Exception e) {
            log.error("파일 조회 실패: " + fileName, e);
            return ResponseEntity.notFound().build();
        }
    }


    @DeleteMapping("/removeFile/{fileName}")
    public ResponseEntity<String> removeFile(@PathVariable String fileName) {
        try {
            // @PathVariable로 받은 fileName은 디코딩된 상태이므로, MinIO 요청을 위해 다시 URL 인코딩
            String encodedFileName = URLEncoder.encode(fileName, "UTF-8").replaceAll("\\+", "%20");
            uploadUtil.deleteFileFromMinio(encodedFileName);
            return ResponseEntity.ok("File deleted successfully");
        } catch (Exception e) {
            log.error("파일 삭제 실패: " + fileName, e);
            return ResponseEntity.status(500).body("Failed to delete file");
        }
    }
}
