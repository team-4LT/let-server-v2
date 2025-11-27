package com.example.let_v2.domain.menu.dto.response

import com.example.let_v2.domain.menu.domain.Menu

data class MenuResponse(
    val menuId : Long,
    val menuName : String
){
    companion object{
        fun of(menu : Menu): MenuResponse {
            return MenuResponse(
                menuId = menu.id!!,
                menuName = menu.name
            )
        }
    }
}