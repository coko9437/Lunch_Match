package com.busanit501.lunch_match.service;

public interface EmailAuthService {
    void sendAuthCode(String email);
    boolean verifyAuthCode(String email, String authCode);

}