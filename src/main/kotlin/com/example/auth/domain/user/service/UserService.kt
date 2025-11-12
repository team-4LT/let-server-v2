package com.example.auth.domain.user.service

import com.example.auth.domain.user.domain.User
import com.example.auth.domain.user.dto.response.GetMeResponse

interface UserService {
    fun getMe(): GetMeResponse
}