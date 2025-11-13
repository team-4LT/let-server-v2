package com.example.auth.global.config.web

import com.example.auth.global.interceptor.RateLimitInterceptor
import org.springframework.context.annotation.Configuration
import org.springframework.web.servlet.config.annotation.InterceptorRegistry
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer

@Configuration
class WebConfig(
    private val rateLimitInterceptor: RateLimitInterceptor
) : WebMvcConfigurer {

    override fun addInterceptors(registry: InterceptorRegistry) {
        registry.addInterceptor(rateLimitInterceptor)
            .addPathPatterns("/auth/**")
            .excludePathPatterns("/auth/health")
    }
}