package com.example.let_v2.domain.auth.controller

import com.example.let_v2.domain.auth.constant.ClientType
import com.example.let_v2.domain.auth.docs.AuthDocs
import com.example.let_v2.domain.auth.dto.request.LoginRequest
import com.example.let_v2.domain.auth.dto.request.ReissueRequest
import com.example.let_v2.domain.auth.dto.request.SignUpRequest
import com.example.let_v2.domain.auth.error.AuthError
import com.example.let_v2.domain.auth.service.AuthService
import com.example.let_v2.global.common.BaseResponse
import com.example.let_v2.global.error.CustomException
import com.example.let_v2.global.security.jwt.response.TokenResponse
import com.example.let_v2.global.security.jwt.util.CookieUtil
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import jakarta.validation.Valid
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PostMapping
import org.springframework.web.bind.annotation.RequestBody
import org.springframework.web.bind.annotation.RequestHeader
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/auth")
class AuthController(
    private val authService: AuthService,
    private val cookieUtil: CookieUtil
) : AuthDocs {

    @PostMapping("/signup")
    override fun signup(
        @Valid @RequestBody signUpRequest: SignUpRequest
    ) : ResponseEntity<BaseResponse.Empty> {
        authService.signup(signUpRequest)
        return BaseResponse.success(HttpStatus.CREATED.value())
    }

    @PostMapping("/login")
    override fun login(
        @Valid @RequestBody loginRequest: LoginRequest,
        @RequestHeader("X-Client-Type", defaultValue = ClientType.WEB) clientType: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        validateClientType(clientType)

        val tokenResponse = authService.login(loginRequest)

        return handleTokenResponse(tokenResponse, clientType, response)
    }

    @PostMapping("/reissue")
    override fun reissue(
        @RequestHeader("X-Client-Type", defaultValue = ClientType.WEB) clientType: String,
        @RequestHeader("Authorization", required = false) authHeader: String?,
        request: HttpServletRequest,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        validateClientType(clientType)

        val refreshToken = extractRefreshToken(request, authHeader, clientType)
        val tokenResponse = authService.reissue(ReissueRequest(refreshToken))

        return handleTokenResponse(tokenResponse, clientType, response)
    }

    @PostMapping("/logout")
    override fun logout(response: HttpServletResponse): ResponseEntity<BaseResponse.Empty> {
        authService.logout()
        cookieUtil.deleteTokenCookies(response)
        return BaseResponse.success(HttpStatus.OK.value())
    }

    private fun validateClientType(clientType: String) {
        if (!ClientType.isValid(clientType)) {
            throw CustomException(AuthError.INVALID_CLIENT_TYPE)
        }
    }

    private fun extractRefreshToken(
        request: HttpServletRequest,
        authHeader: String?,
        clientType: String
    ): String {
        val tokenFromCookie = cookieUtil.getRefreshTokenFromCookie(request)
        val tokenFromHeader = authHeader?.removePrefix("Bearer ")?.takeIf { it.isNotBlank() }

        return tokenFromCookie ?: tokenFromHeader ?: run {
            throw CustomException(AuthError.REFRESH_TOKEN_NOT_FOUND)
        }
    }

    private fun handleTokenResponse(
        tokenResponse: TokenResponse,
        clientType: String,
        response: HttpServletResponse
    ): ResponseEntity<*> {
        return if (ClientType.isApp(clientType)) {
            // 앱: JSON으로 토큰 반환
            BaseResponse.of(
                data = tokenResponse,
                status = HttpStatus.OK.value()
            )
        } else {
            // 웹: HTTP-Only 쿠키에 토큰 저장
            cookieUtil.addTokenCookies(response, tokenResponse.accessToken, tokenResponse.refreshToken)
            BaseResponse.success(HttpStatus.OK.value())
        }
    }
}