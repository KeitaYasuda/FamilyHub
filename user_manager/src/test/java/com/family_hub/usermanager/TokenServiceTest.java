package com.familyhub.usermanager;

import com.familyhub.usermanager.model.User;
import com.familyhub.usermanager.service.TokenService;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
class TokenServiceTest {

    @Autowired
    private TokenService tokenService;

    // ユーザ情報から生成したトークンが正しく検証でき、ユーザ名が取得できることをテスト
    @Test
    void testGenerateAndValidateToken() {
        User user = new User();
        user.setUsername("testuser");
        user.setEmail("test@example.com");
        user.setRoles("ROLE_USER");

        // JWT トークン生成
        String token = tokenService.generateToken(user);
        Assertions.assertNotNull(token, "JWT token should not be null");

        // 生成したトークンが有効であることを確認
        Assertions.assertTrue(tokenService.validateToken(token), "Token should be valid");

        // トークンから正しくユーザ名が取得できるか検証
        String username = tokenService.getUsernameFromToken(token);
        Assertions.assertEquals("testuser", username, "Username extracted from token should be 'testuser'");
    }
}
