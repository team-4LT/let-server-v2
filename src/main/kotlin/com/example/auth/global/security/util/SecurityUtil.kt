package com.example.auth.global.security.util

import com.example.auth.domain.user.domain.User
import com.example.auth.domain.user.error.UserError
import com.example.auth.domain.user.repository.UserRepository
import com.example.auth.global.error.CustomException
import org.springframework.security.core.context.SecurityContextHolder
import org.springframework.stereotype.Component

@Component
class SecurityUtil(
    private val userRepository: UserRepository
) {
    fun getCurrentUser(): User{
        val authentication = SecurityContextHolder.getContext().authentication
            ?: throw CustomException(UserError.USER_NOT_FOUND)

        val username = authentication.name
        return userRepository.findByName(username)
            ?: throw CustomException(UserError.USER_NOT_FOUND)
    }
}