package com.busanit501.lunch_match.dto;

import lombok.Data;

@Data
public class EmailAuthVerifyDTO {
    private String email;
    private String authCode;
}
