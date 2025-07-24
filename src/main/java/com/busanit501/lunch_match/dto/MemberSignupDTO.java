package com.busanit501.lunch_match.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class MemberSignupDTO {

//    Member 엔티티의 username에 매핑
    @NotBlank(message = "아이디는 필수 입력 값입니다.")
    @Size(min = 4, max = 20, message = "아이디는 4자 이상 20자 이하로 입력해주세요.")
    @Pattern(regexp = "^[a-z0-9]+$", message = "아이디는 영소문자 또는 숫자만 가능합니다.")
    private String username;

    // 비밀번호
    @NotBlank(message = "비밀번호는 필수 입력 값입니다.")
    @Size(min = 8, max = 20, message = "비밀번호는 8자 이상 20자 이하로 입력해주세요.")
    private String password;

//    비밀번호 확인
    private String confirmPassword;

    @NotBlank(message = "이름은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "이름은 2자 이상 10자 이하로 입력해주세요.")
    private String name;

    // 생년월일 (YYYYMMDD 형태)
    @NotNull(message = "생년월일은 필수 입력 값입니다.")
    private LocalDate birthDate;

    @NotBlank(message = "이메일은 필수 입력 값입니다.")
    @Email(message = "유효한 이메일 주소를 입력해주세요.")
    @Size(max = 20, message = "이메일 주소는 20자 이하로 입력해주세요.")
    private String email;

    // 닉네임 (중복 방지를 위한 랜덤 숫자 생성은 서비스/엔티티에서 처리)
    @NotBlank(message = "닉네임은 필수 입력 값입니다.")
    @Size(min = 2, max = 10, message = "닉네임은 2자 이상 10자 이하로 입력해주세요.")
    private String nickname;



}
