package com.example.let_v2.domain.auth.docs

import com.example.let_v2.domain.auth.dto.request.LoginRequest
import com.example.let_v2.domain.auth.dto.request.SignUpRequest
import com.example.let_v2.global.common.BaseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.security.SecurityRequirement
import io.swagger.v3.oas.annotations.tags.Tag
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.ResponseEntity

@Tag(name = "Auth", description = "인증 API")
interface AuthDocs {

    @Operation(summary = "회원가입", description = "새로운 사용자를 등록합니다")
    fun signup(signUpRequest: SignUpRequest): ResponseEntity<BaseResponse.Empty>

    @Operation(
        summary = "로그인",
        description = "이메일과 비밀번호로 로그인합니다. 웹: HTTP-Only 쿠키, 앱(X-Client-Type: app): JSON으로 토큰 반환"
    )
    fun login(loginRequest: LoginRequest, clientType: String, response: HttpServletResponse): ResponseEntity<*>

    @Operation(
        summary = "토큰 재발급",
        description = "리프레시 토큰으로 새로운 액세스 토큰을 발급받습니다. 웹: 쿠키에서 추출, 앱: Authorization 헤더에서 추출",
        security = [SecurityRequirement(name = "bearerAuth")]
    )
    fun reissue(clientType: String, authHeader: String?, request: HttpServletRequest, response: HttpServletResponse): ResponseEntity<*>

    @Operation(
        summary = "로그아웃",
        description = "사용자를 로그아웃 처리하고 쿠키를 삭제합니다"
    )
    fun logout(response: HttpServletResponse): ResponseEntity<BaseResponse.Empty>
}