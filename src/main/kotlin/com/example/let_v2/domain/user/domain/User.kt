package com.example.let_v2.domain.user.domain

class User(
    val id: Long ?= null,
    val name: String,
    val password: String,
    val role: UserRole,
    val studentId: Int,
    val realName: String
)