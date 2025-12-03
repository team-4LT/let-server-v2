package com.example.let_v2.domain.user.repository

import com.example.let_v2.domain.user.domain.User
import com.example.let_v2.domain.user.error.UserError
import com.example.let_v2.global.error.CustomException

interface UserRepository {
    fun save(user: User): User
    fun findByName(name: String): User?
    fun existsByName(name: String): Boolean
    fun findAllStudents(): List<User>
    fun findById(userId: Long) : User?
}

fun UserRepository.findByNameOrThrow(username: String): User {
    return findByName(username)
        ?: throw CustomException(UserError.USER_NOT_FOUND)
}

fun UserRepository.findByIdOrThrow(userId: Long): User {
    return findById(userId)
        ?: throw CustomException(UserError.USER_NOT_FOUND)
}