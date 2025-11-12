package com.example.auth.domain.user.repository

import com.example.auth.domain.user.domain.User
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository

@Repository
interface UserRepository: JpaRepository<User, Long> {
    fun existsByName(name: String): Boolean
    fun findByName(name: String): User?
}