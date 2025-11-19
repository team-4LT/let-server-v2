package com.example.let_v2.global.security.csrf

import com.example.let_v2.domain.auth.constant.ClientType
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.web.csrf.CsrfToken
import org.springframework.web.filter.OncePerRequestFilter

class CsrfHeaderFilter : OncePerRequestFilter(){
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        val clientType = request.getHeader("X-Client-Type") ?: ClientType.WEB

        // 앱 클라이언트는 CSRF 토큰 불필요
        if (ClientType.isApp(clientType)) {
            request.setAttribute("_csrf", null)
        } else {
            // 웹: CSRF 토큰을 응답 헤더로 전송
            val csrfToken = request.getAttribute(CsrfToken::class.java.name) as? CsrfToken
            csrfToken?.let {
                response.setHeader(it.headerName, it.token)
            }
        }
        filterChain.doFilter(request, response)
    }
}