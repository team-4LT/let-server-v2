package com.example.auth.domain.user.service.impl

import com.example.auth.domain.user.dto.response.GetMeResponse
import com.example.auth.domain.user.service.UserService
import com.example.auth.global.security.util.SecurityUtil
import org.springframework.stereotype.Service

@Service
class UserServiceImpl(
    private val securityUtil: SecurityUtil
): UserService {
    override fun getMe(): GetMeResponse {
        return GetMeResponse.of(securityUtil.getCurrentUser())
    }
}