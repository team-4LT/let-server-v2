package com.example.let_v2.domain.meal.docs

import com.example.let_v2.domain.meal.dto.response.GetDailyMealResponse
import com.example.let_v2.domain.meal.dto.response.GetMonthlyMealResponse
import com.example.let_v2.global.common.BaseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.format.annotation.DateTimeFormat
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestParam
import java.util.Date

@Tag(name = "meals", description = "급식 관련 API")
interface MealDocs {
    @Operation(summary = "현재 달의 급식 조회")
    fun getMonthlyMenu(
        @PathVariable period: String,
        @Parameter(
            description = "알러지 ID 리스트",
            `in` = ParameterIn.QUERY,
            required = false,
            example = "1,2,5"
        )
        @RequestParam(required = false) allergyIds: List<Int>?
    ): ResponseEntity<BaseResponse.Success<List<GetMonthlyMealResponse>>>

    @Operation(summary = "하루치 급식 조회")
    fun getDailyMenu(
        @PathVariable
        @DateTimeFormat(pattern = "yyyy-MM-dd")
        today: Date
    ): ResponseEntity<BaseResponse.Success<List<GetDailyMealResponse>>>
}