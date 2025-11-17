package com.example.let_v2.domain.user.service

import com.example.let_v2.domain.user.dto.response.GetMeResponse
import com.example.let_v2.global.security.util.SecurityUtil
import org.springframework.stereotype.Service

@Service
class UserService(
    private val securityUtil: SecurityUtil
) {
    fun getMe(): GetMeResponse {
        return GetMeResponse.Companion.of(securityUtil.getCurrentUser())
    }
}