package com.example.let_v2.domain.user.domain

import com.example.let_v2.domain.allergy.Allergy

class UserAllergy(
    val userId: Long,
    val allergy: Allergy
){
    companion object {
        fun of(userId: Long, allergy: Allergy) = UserAllergy(userId, allergy)
    }
}