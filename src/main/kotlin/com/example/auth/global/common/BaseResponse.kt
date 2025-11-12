package com.example.auth.global.common

import org.springframework.http.ResponseEntity

data class BaseResponse<T>(
    val status: Int,
    val message: String,
    val data: T? = null
){
    companion object {
        fun <T> of(
            data: T?,
            status: Int = 200,
            message: String = if (status in 200..299) "Success" else "Fail"
        ): ResponseEntity<BaseResponse<T>> {
            return ResponseEntity
                .status(status)
                .body(BaseResponse(status, message, data))
        }
    }
}
