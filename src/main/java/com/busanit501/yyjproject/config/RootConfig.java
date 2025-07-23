package com.busanit501.yyjproject.config;

import com.busanit501.yyjproject.domain.UploadResult;
import com.busanit501.yyjproject.dto.UploadResultDTO;
import org.modelmapper.ModelMapper;
import org.modelmapper.convention.MatchingStrategies;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import com.busanit501.yyjproject.domain.UploadResult;
import com.busanit501.yyjproject.dto.UploadResultDTO;

@Configuration
public class RootConfig {

    @Bean
    public ModelMapper getMapper() {
        ModelMapper modelMapper = new ModelMapper();
        modelMapper.getConfiguration()
                .setFieldMatchingEnabled(true)
                .setFieldAccessLevel(org.modelmapper.config.Configuration.AccessLevel.PRIVATE)
                .setMatchingStrategy(MatchingStrategies.STRICT);

        // UploadResult.isImage -> UploadResultDTO.img 매핑 설정
        modelMapper.createTypeMap(com.busanit501.yyjproject.domain.UploadResult.class, com.busanit501.yyjproject.dto.UploadResultDTO.class)
                .addMapping(UploadResult::isImage, UploadResultDTO::setImg);

        // UploadResultDTO.img -> UploadResult.isImage 매핑 설정
        modelMapper.createTypeMap(com.busanit501.yyjproject.dto.UploadResultDTO.class, com.busanit501.yyjproject.domain.UploadResult.class)
                .addMapping(UploadResultDTO::isImg, UploadResult::setIsImage);

        return modelMapper;
    }
}
