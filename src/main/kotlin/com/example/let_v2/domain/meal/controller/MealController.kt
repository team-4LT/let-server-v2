package com.example.let_v2.domain.meal.controller

import com.example.let_v2.domain.meal.dto.response.GetMonthlyMealResponse
import com.example.let_v2.domain.meal.service.MealService
import com.example.let_v2.global.common.BaseResponse
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.enums.ParameterIn
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/meals")
class MealController(
    private val mealService: MealService
) {
    @GetMapping("/{period}")
    fun getMonthlyMenu(
        @PathVariable period: String,
        @Parameter(
            description = "알러지 ID 리스트",
            `in` = ParameterIn.QUERY,
            required = false,
            example = "1,2,5"
        )
        @RequestParam(required = false) allergyIds: List<Int>?
    ): ResponseEntity<BaseResponse.Success<List<GetMonthlyMealResponse>>> {

        val response = mealService.getMonthlyMenu(period, allergyIds)
        return BaseResponse.of(response, HttpStatus.OK.value())
    }
}