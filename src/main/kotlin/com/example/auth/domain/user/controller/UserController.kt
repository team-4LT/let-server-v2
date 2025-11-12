package com.example.auth.domain.user.controller

import com.example.auth.domain.user.dto.response.GetMeResponse
import com.example.auth.domain.user.service.UserService
import com.example.auth.global.common.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
) {
    @GetMapping("/me")
    fun getMe(): ResponseEntity<BaseResponse<GetMeResponse>>{
        return BaseResponse.of(userService.getMe(), HttpStatus.OK.value())
    }
}