package com.example.let_v2.domain.eater.docs

import com.example.let_v2.domain.eater.dto.response.EaterResponse
import com.example.let_v2.global.common.BaseResponse
import io.swagger.v3.oas.annotations.Operation
import io.swagger.v3.oas.annotations.Parameter
import io.swagger.v3.oas.annotations.tags.Tag
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.PathVariable

@Tag(name = "eater", description = "식사자 관련 API")
interface EaterDocs {
    @Operation(summary = "학년별 식사자 조회", description = "특정 학년의 식사자 목록을 조회합니다.")
    fun getEaterByGrade(
        @Parameter(description = "학년 (1, 2, 3)", example = "1")
        @PathVariable grade: Int
    ): ResponseEntity<BaseResponse.Success<List<EaterResponse>>>
}