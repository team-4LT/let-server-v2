package com.example.auth.global.security.jwt.filter

import com.example.auth.global.error.CustomException
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.servlet.FilterChain
import jakarta.servlet.http.HttpServletRequest
import jakarta.servlet.http.HttpServletResponse
import org.springframework.http.MediaType
import org.springframework.stereotype.Component
import org.springframework.web.filter.OncePerRequestFilter

@Component
class JwtExceptionFilter(
    private val objectMapper: ObjectMapper
): OncePerRequestFilter() {
    override fun doFilterInternal(
        request: HttpServletRequest,
        response: HttpServletResponse,
        filterChain: FilterChain
    ) {
        try {
            filterChain.doFilter(request, response)
        }catch (e: CustomException){
            setErrorResponse(response, e)
        }
    }

    private fun setErrorResponse(
        response: HttpServletResponse,
        e: CustomException
    ) {
        response.status = e.error.status
        response.contentType = MediaType.APPLICATION_JSON_VALUE
        response.characterEncoding = "UTF-8"

        val errorResponse = mapOf(
            "status" to e.error.status,
            "message" to e.error.message
        )

        response.writer.write(objectMapper.writeValueAsString(errorResponse))
    }
}