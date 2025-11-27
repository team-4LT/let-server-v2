package com.example.let_v2.domain.menu.domain

import com.example.let_v2.domain.allergy.Allergy

data class MenuAllergy (
    val menuId : Long,
    val allergy: Allergy
)