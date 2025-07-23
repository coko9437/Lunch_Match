package com.busanit501.yyjproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class UploadResultDTO {
    private String uuid;
    private String fileName;
    private boolean img;

    public String getLink() {
        if(img){
            return "s_" + uuid + "_" + fileName; // 썸네일 경로
        } else {
            return uuid + "_" + fileName;
        }
    }
}
