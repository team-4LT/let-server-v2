package com.example.let_v2.domain.menu.repository.allergy

import com.example.let_v2.domain.menu.domain.MenuAllergy

interface MenuAllergyRepository {
    fun saveAllBatch(menuAllergyList: List<MenuAllergy>)
    fun findAllByMenuIdIn(menuIds: List<Long>): List<MenuAllergy>
}