package com.orange.edu.system.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;

/**
 * 跨域过滤器
 */
@Configuration
public class GlobalCorsConfig {

    @Bean
    public CorsFilter corsFilter() {
        CorsConfiguration configuration = new CorsConfiguration();
        // 哪些http方法可以跨域，比如：GET、POST、PUT等，多个方法之间用逗号隔开
        configuration.addAllowedMethod("*");
        // 允许哪些请求进行跨域，*表示所有，可以具体指定 http://localhost:8601/ 跨域 允许白名单域名进行跨域调用
        configuration.addAllowedOrigin("*");
        // 所有头信息全部放行
        configuration.addAllowedHeader("*");
        // 允许跨域发送 cookie
        configuration.setAllowCredentials(true);
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", configuration);
        return new CorsFilter(source);
    }

}
