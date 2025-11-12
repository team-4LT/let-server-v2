package com.example.auth.global.security.user

import com.example.auth.domain.user.error.UserError
import com.example.auth.domain.user.repository.UserRepository
import com.example.auth.global.error.CustomException
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userRepository.findByName(username)
            ?: throw CustomException(UserError.USER_NOT_FOUND)
        return CustomUserDetails(user)
    }
}