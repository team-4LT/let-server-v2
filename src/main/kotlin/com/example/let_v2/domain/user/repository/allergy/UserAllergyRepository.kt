package com.example.let_v2.domain.user.repository.allergy

import com.example.let_v2.domain.user.domain.UserAllergy

interface UserAllergyRepository {
    fun saveAll(userAllergies: List<UserAllergy>)
}