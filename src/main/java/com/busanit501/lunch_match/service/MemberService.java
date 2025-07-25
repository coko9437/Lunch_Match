package com.busanit501.lunch_match.service;

import com.busanit501.lunch_match.dto.MemberSignupDTO;
import com.busanit501.lunch_match.dto.ProfileDTO;

public interface MemberService {
    Long SignupMember(MemberSignupDTO memberSignupDTO, ProfileDTO profileDTO);

    // 사용자 ID 중복 확인
    boolean nameExists(String username);

    // 닉네임 중복 확인
    boolean nicknameExists(String nickname);

    // 이메일 중복 확인
    boolean emailExists(String email);

    // 전화번호 중복 확인
    boolean phoneNumberExists(String phoneNumber);


}

