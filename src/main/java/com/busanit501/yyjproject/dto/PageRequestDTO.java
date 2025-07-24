package com.busanit501.yyjproject.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;
import lombok.NoArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.ToString;
import lombok.experimental.SuperBuilder;

@SuperBuilder
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@ToString
@EqualsAndHashCode(callSuper = false)
public class PageRequestDTO extends SearchRequestDTO {

    @Builder.Default
    private int page = 1;

    @Builder.Default
    private int size = 10;

    public int getSkip() {
        return (page - 1) * size;
    }

    // Sort 객체를 선택적으로 받도록 수정
    public org.springframework.data.domain.Pageable getPageable(org.springframework.data.domain.Sort sort) {
        if (sort == null) {
            return org.springframework.data.domain.PageRequest.of(page - 1, size);
        }
        return org.springframework.data.domain.PageRequest.of(page - 1, size, sort);
    }
}
