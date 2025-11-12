package com.example.auth.global.error

import com.example.auth.global.common.BaseResponse
import java.lang.RuntimeException

class CustomException(
    val error: CustomError,
): RuntimeException(error.message)