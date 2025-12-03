package com.example.let_v2.domain.user.dto.response

import com.example.let_v2.domain.user.domain.User

data class UserEaterResponse(
    val userId : Long,
    val studentId : Int,
    val realName : String
){
    companion object{
        fun of(user : User): UserEaterResponse {
            return UserEaterResponse(
                userId = user.id!!,
                studentId = user.studentId,
                realName = user.name
            )
        }
    }
}
