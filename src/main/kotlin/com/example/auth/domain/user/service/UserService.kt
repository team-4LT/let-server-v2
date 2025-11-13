package com.example.auth.domain.user.service

import com.example.auth.domain.user.dto.response.GetMeResponse
import com.example.auth.global.security.util.SecurityUtil
import org.springframework.stereotype.Service

@Service
class UserService(
    private val securityUtil: SecurityUtil
) {
    fun getMe(): GetMeResponse {
        return GetMeResponse.Companion.of(securityUtil.getCurrentUser())
    }
}