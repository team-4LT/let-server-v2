package com.example.let_v2.domain.auth.repository

import com.example.let_v2.domain.auth.domain.RefreshToken
import org.springframework.data.repository.CrudRepository

interface RefreshTokenRepository : CrudRepository<RefreshToken, String> {
    fun findByUsername(username: String): RefreshToken?
}