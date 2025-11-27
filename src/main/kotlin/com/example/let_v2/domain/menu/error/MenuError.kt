package com.example.let_v2.domain.menu.error

import com.example.let_v2.global.error.CustomError
import org.springframework.http.HttpStatus

enum class MenuError(
    override val message: String,
    override val status: Int
): CustomError {
    MENU_NOT_FOUND("Menu not found.", HttpStatus.NOT_FOUND.value())
}