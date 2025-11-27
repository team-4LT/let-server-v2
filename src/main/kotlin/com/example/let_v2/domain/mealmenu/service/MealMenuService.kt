package com.example.let_v2.domain.mealmenu.service

import com.example.let_v2.domain.meal.domain.MealType
import com.example.let_v2.domain.meal.dto.MealInfo
import com.example.let_v2.domain.mealmenu.dto.DateRange
import com.example.let_v2.domain.mealmenu.dto.MenuInfo
import com.fasterxml.jackson.databind.ObjectMapper
import kotlinx.coroutines.*
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.client.RestTemplate
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class MealMenuService(
    private val restTemplate: RestTemplate,
    private val transactionService: MealMenuTransactionService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(MealMenuService::class.java)
    private val API_RESPONSE_DATA_INDEX = 1
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")

    @Value("\${KEY}")
    private lateinit var apiKey: String

    @Scheduled(cron = "0 0 0 1 * ?")
    fun initializeMealData() {
        CoroutineScope(Dispatchers.Default).launch {
            try {
                fetchAndSaveMonthlyMeals()
            } catch (e: Exception) {
                logger.error("급식 데이터 초기화 실패", e)
            }
        }
    }

    suspend fun fetchAndSaveMonthlyMeals() {
        val now = LocalDate.now()
        val dateRange = createDateRange(now)

        val allMeals = coroutineScope {
            MealType.entries.map { mealType ->
                async(Dispatchers.IO) {
                    try {
                        fetchAndParseMealData(dateRange, mealType)
                    } catch (e: Exception) {
                        logger.error("급식 데이터 수집 실패. MealType: ${mealType.value}", e)
                        emptyList<MealInfo>()
                    }
                }
            }.awaitAll().flatten()
        }

        if (allMeals.isNotEmpty()) {
            transactionService.saveMealsInBulk(allMeals)
        } else {
            logger.warn("수집된 급식 데이터가 없습니다.")
        }
    }

    private suspend fun fetchAndParseMealData(dateRange: DateRange, mealType: MealType): List<MealInfo> {
        val url = buildApiUrl(dateRange, mealType)
        val response = withContext(Dispatchers.IO) {
            restTemplate.getForEntity(url, String::class.java)
        }

        return if (response.statusCode.is2xxSuccessful && response.body != null) {
            parseMealData(response.body!!, mealType)
        } else {
            logger.warn("API 호출 실패. MealType: ${mealType.value}, Status: ${response.statusCode}")
            emptyList()
        }
    }

    private fun buildApiUrl(dateRange: DateRange, mealType: MealType): String {
        val OFFICE_CODE = "D10"
        val SCHOOL_CODE = "7240454"
        val PAGE_SIZE = 100
        return "https://open.neis.go.kr/hub/mealServiceDietInfo" +
                "?ATPT_OFCDC_SC_CODE=$OFFICE_CODE&SD_SCHUL_CODE=$SCHOOL_CODE" +
                "&KEY=$apiKey&MLSV_FROM_YMD=${dateRange.from}&MLSV_TO_YMD=${dateRange.to}" +
                "&Type=json&MMEAL_SC_CODE=${mealType.toInt()}&pSize=$PAGE_SIZE"
    }

    private fun createDateRange(date: LocalDate): DateRange {
        val from = date.withDayOfMonth(1).format(DATE_FORMATTER)
        val to = date.withDayOfMonth(date.lengthOfMonth()).format(DATE_FORMATTER)
        return DateRange(from, to)
    }

    private fun parseMealData(json: String, mealType: MealType): List<MealInfo> {
        val root = objectMapper.readTree(json)
        val rows = root.path("mealServiceDietInfo")
            .path(API_RESPONSE_DATA_INDEX)
            .path("row")

        if (rows.isMissingNode || !rows.isArray) {
            logger.warn("API 응답에서 row 데이터를 찾을 수 없음. MealType: ${mealType.value}")
            return emptyList()
        }

        val allergyPattern = "\\((\\d+(?:\\.\\d+)*)\\)".toRegex()

        return rows.map { row ->
            val mealDate = row.path("MLSV_YMD").asText()
            val menuWithAllergiesRaw = row.path("DDISH_NM").asText()
            val calories = row.path("CAL_INFO").asText()
                .replace("Kcal", "")
                .trim()
                .toFloatOrNull() ?: 0f

            val menus = menuWithAllergiesRaw.split("<br/>").mapNotNull { menuString ->
                val cleanedMenuString = menuString.trim()
                if (cleanedMenuString.isNotEmpty()) {
                    val menuName = allergyPattern.replace(cleanedMenuString, "").trim()
                    if (menuName.isEmpty()) {
                        null
                    } else {
                        val allergies = allergyPattern.findAll(cleanedMenuString)
                            .flatMap { it.groupValues[1].split('.') }
                            .mapNotNull { it.toIntOrNull() }
                            .toList()
                        MenuInfo(menuName, allergies)
                    }
                } else {
                    null
                }
            }

            MealInfo(mealDate, mealType, calories, menus)
        }
    }
}