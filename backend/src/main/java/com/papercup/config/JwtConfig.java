package com.papercup.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置类
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtConfig {
    
    /**
     * JWT密钥
     */
    private String secret = "papercup-sales-secret-key-2024";
    
    /**
     * JWT过期时间（毫秒）
     */
    private Long expiration = 86400000L; // 24小时
    
    /**
     * JWT请求头
     */
    private String header = "Authorization";
    
    /**
     * JWT前缀
     */
    private String prefix = "Bearer ";
}
