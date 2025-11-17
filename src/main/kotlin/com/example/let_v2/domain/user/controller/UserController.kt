package com.example.let_v2.domain.user.controller

import com.example.let_v2.domain.user.docs.UserDocs
import com.example.let_v2.domain.user.dto.response.GetMeResponse
import com.example.let_v2.domain.user.service.UserService
import com.example.let_v2.global.common.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/users")
class UserController(
    private val userService: UserService
): UserDocs {
    @GetMapping("/me")
    override fun getMe(): ResponseEntity<BaseResponse.Success<GetMeResponse>>{
        return BaseResponse.of(userService.getMe(), HttpStatus.OK.value())
    }
}