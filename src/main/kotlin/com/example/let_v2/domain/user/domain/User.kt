package com.example.let_v2.domain.user.domain

import com.example.let_v2.domain.allergy.Allergy

data class User(
    val id: Long? = null,
    val name: String,
    val password: String,
    val role: UserRole,
    val studentId: Int,
    val realName: String,
    val allergies: List<Allergy> = emptyList()
)