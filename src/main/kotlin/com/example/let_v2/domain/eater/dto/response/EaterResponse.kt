package com.example.let_v2.domain.eater.dto.response

import com.example.let_v2.domain.eater.domain.Eater
import com.example.let_v2.domain.user.dto.response.UserEaterResponse

data class EaterResponse(
    val eaterId : Long,
    val user : UserEaterResponse,
    val eaten : Boolean
){
    companion object {
        fun of(eater: Eater, user: UserEaterResponse): EaterResponse {
            return EaterResponse(
                eaterId = eater.id!!,
                user = user,
                eaten = eater.eaten
            )
        }
    }
}
