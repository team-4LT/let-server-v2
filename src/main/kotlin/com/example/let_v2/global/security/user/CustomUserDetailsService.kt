package com.example.let_v2.global.security.user

import com.example.let_v2.domain.user.repository.UserRepository
import com.example.let_v2.domain.user.repository.findByNameOrThrow
import org.springframework.security.core.userdetails.UserDetails
import org.springframework.security.core.userdetails.UserDetailsService

class CustomUserDetailsService(
    private val userRepository: UserRepository
) : UserDetailsService {
    override fun loadUserByUsername(username: String): UserDetails? {
        val user = userRepository.findByNameOrThrow(username)
        return CustomUserDetails(user)
    }
}