package com.example.let_v2.domain.menu.repository

import com.example.let_v2.domain.menu.domain.Menu
import com.example.let_v2.domain.menu.error.MenuError
import com.example.let_v2.global.error.CustomException

interface MenuRepository {
    fun save(menu: Menu) : Menu
    fun saveAll(menus: List<Menu>): List<Menu>
    fun findAllByNameIn(names: List<String>): List<Menu>
    fun findByName(name: String) : Menu?
    fun findById(id: Long) : Menu?
}

fun MenuRepository.findByIdOrThrow(id: Long): Menu {
    return findById(id) ?: throw CustomException(MenuError.MENU_NOT_FOUND)
}