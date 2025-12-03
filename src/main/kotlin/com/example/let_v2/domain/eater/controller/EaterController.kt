package com.example.let_v2.domain.eater.controller

import com.example.let_v2.domain.eater.docs.EaterDocs
import com.example.let_v2.domain.eater.dto.response.EaterResponse
import com.example.let_v2.domain.eater.service.EaterService
import com.example.let_v2.global.common.BaseResponse
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.PathVariable
import org.springframework.web.bind.annotation.RequestMapping
import org.springframework.web.bind.annotation.RestController

@RestController
@RequestMapping("/eaters")
class EaterController(
    private val eaterService: EaterService
): EaterDocs {
    @GetMapping("/{grade}")
    override fun getEaterByGrade(
        @PathVariable grade: Int
    ): ResponseEntity<BaseResponse.Success<List<EaterResponse>>>{
        val response = eaterService.findByGrade(grade)
        return BaseResponse.of(response, HttpStatus.OK.value())
    }
}