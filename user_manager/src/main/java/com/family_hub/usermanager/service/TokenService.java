package com.familyhub.usermanager.service;

import com.familyhub.usermanager.model.User;
import io.jsonwebtoken.*;
import java.util.Date;
import org.springframework.stereotype.Service;

@Service
public class TokenService {

    // 本番環境ではシークレット鍵は外部設定（例：環境変数）から取得すること
    private final String jwtSecret = "SuperSecretKeyForJWT";
    private final long jwtExpirationMs = 3600000; // 1時間の有効期限

    // JWT 生成
    public String generateToken(User user) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + jwtExpirationMs);

        return Jwts.builder()
                .setSubject(user.getUsername())
                .claim("roles", user.getRoles())
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // JWT からユーザ名を取得
    public String getUsernameFromToken(String token) {
        return Jwts.parser()
                .setSigningKey(jwtSecret)
                .parseClaimsJws(token)
                .getBody()
                .getSubject();
    }

    // トークンの検証
    public boolean validateToken(String token) {
        try {
            Jwts.parser().setSigningKey(jwtSecret).parseClaimsJws(token);
            return true;
        } catch (JwtException | IllegalArgumentException ex) {
            // 無効なトークンの場合は例外が発生するので false を返す
            return false;
        }
    }
}
