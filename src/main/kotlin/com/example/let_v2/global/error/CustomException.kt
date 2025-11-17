package com.example.let_v2.global.error

import java.lang.RuntimeException

class CustomException(
    val error: CustomError,
): RuntimeException(error.message)