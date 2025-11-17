package com.example.let_v2.domain.user.dto.response

import com.example.let_v2.domain.user.domain.User
import com.example.let_v2.domain.user.domain.UserRole

data class GetMeResponse(
    val id: Long,
    val name: String,
    val role: UserRole,
    val studentId: Int,
    val realName: String
){
    companion object {
        fun of(user: User): GetMeResponse {
            return GetMeResponse(
                id = user.id!!,
                name = user.name,
                role = user.role,
                studentId = user.studentId,
                realName = user.realName
            )
        }
    }
}
