package com.familyhub.usermanager;

import com.familyhub.usermanager.repository.UserRepository;
import com.familyhub.usermanager.service.AuthService;
import com.fasterxml.jackson.databind.ObjectMapper;
import java.util.Map;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthService authService;

    private final ObjectMapper objectMapper = new ObjectMapper();

    @BeforeEach
    void setup() {
        // 各テスト実行前に DB をクリーンアップ
        userRepository.deleteAll();
    }

    // ユーザ登録が成功することのテスト
    @Test
    void testRegister() throws Exception {
        String json = "{\"username\": \"testuser\", \"password\": \"testpass\", \"email\": \"test@example.com\"}";
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(content().string("User registered: testuser"));
    }

    // 同一ユーザ名での登録はエラーとなることのテスト
    @Test
    void testRegisterDuplicateUser() throws Exception {
        // 事前にユーザ登録しておく
        authService.register("testuser", "testpass", "test@example.com");

        String json = "{\"username\": \"testuser\", \"password\": \"testpass\", \"email\": \"test@example.com\"}";
        mockMvc.perform(post("/auth/register")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isBadRequest());
    }

    // 正しい資格情報でログインでき、JWT が発行されることのテスト
    @Test
    void testLogin() throws Exception {
        authService.register("testuser", "testpass", "test@example.com");

        String json = "{\"username\": \"testuser\", \"password\": \"testpass\"}";
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists());
    }

    // 誤ったパスワードの場合は認証エラーとなるテスト
    @Test
    void testLoginInvalidPassword() throws Exception {
        authService.register("testuser", "testpass", "test@example.com");

        String json = "{\"username\": \"testuser\", \"password\": \"wrongpass\"}";
        mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(json))
                .andExpect(status().isUnauthorized());
    }

    // 取得した JWT を使用して /auth/userinfo エンドポイントから認証済みユーザ情報を取得するテスト
    @Test
    void testUserInfo() throws Exception {
        // ユーザ登録
        authService.register("testuser", "testpass", "test@example.com");

        // ログインして JWT を取得
        String loginJson = "{\"username\": \"testuser\", \"password\": \"testpass\"}";
        String loginResponse = mockMvc.perform(post("/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content(loginJson))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.accessToken").exists())
                .andReturn()
                .getResponse()
                .getContentAsString();

        // JSON 文字列から accessToken を抽出
        @SuppressWarnings("unchecked")
        Map<String, String> result = objectMapper.readValue(loginResponse, Map.class);
        String accessToken = result.get("accessToken");

        // 取得したアクセストークンを使用して、/auth/userinfo を呼び出し、認証済みユーザ情報を取得
        mockMvc.perform(get("/auth/userinfo")
                .header("Authorization", "Bearer " + accessToken))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.username").value("testuser"))
                .andExpect(jsonPath("$.authorities").isArray());
    }
}
