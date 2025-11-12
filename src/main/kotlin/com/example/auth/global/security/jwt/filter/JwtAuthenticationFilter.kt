package com.example.auth.global.security.jwt.filter

import com.example.auth.global.security.jwt.provider.JwtProvider
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtAuthenticationFilter(
    private val jwtProvider: JwtProvider
) : OncePerRequestFilter() {

    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            val token = jwtProvider.extractToken(request)

            token?.let {
                val authentication = jwtProvider.getAuthentication(it)
                SecurityContextHolder.getContext().authentication = authentication
            }
        } catch (e: Exception) {
            SecurityContextHolder.clearContext()
        }

        filterChain.doFilter(request, response)
    }
}