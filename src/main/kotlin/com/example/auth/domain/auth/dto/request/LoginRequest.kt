package com.example.auth.domain.auth.dto.request

import jakarta.validation.constraints.NotBlank

data class LoginRequest(
    @field:NotBlank(message = "이름은 필수입니다")
    val name: String,
    @field:NotBlank(message = "비밀번호는 필수입니다")
    val password: String
)