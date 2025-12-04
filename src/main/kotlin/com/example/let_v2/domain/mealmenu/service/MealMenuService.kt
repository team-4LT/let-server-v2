package com.example.let_v2.domain.mealmenu.service

import com.example.let_v2.domain.meal.domain.MealType
import com.example.let_v2.domain.meal.dto.MealInfo
import com.example.let_v2.domain.mealmenu.dto.MenuInfo
import com.fasterxml.jackson.databind.ObjectMapper
import jakarta.annotation.PostConstruct
import kotlinx.coroutines.*
import kotlinx.coroutines.sync.withPermit
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Value
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.web.reactive.function.client.WebClient
import org.springframework.web.reactive.function.client.awaitBody
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class MealMenuService(
    private val webClient: WebClient,
    private val transactionService: MealDataPersistenceService,
    private val objectMapper: ObjectMapper
) {
    private val logger = LoggerFactory.getLogger(MealMenuService::class.java)
    private val API_RESPONSE_DATA_INDEX = 1
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")
    private val CONCURRENT_REQUESTS = 10

    @Value("\${KEY}")
    private lateinit var apiKey: String

    @Scheduled(cron = "0 0 0 1 * ?")
    @PostConstruct
    fun initializeMealData() {
        CoroutineScope(Dispatchers.Default).launch {
            val start = System.currentTimeMillis()
            try {
                fetchAndSaveMonthlyMeals()
            } catch (e: Exception) {
                logger.error("급식 데이터 초기화 실패", e)
            }
            val end = System.currentTimeMillis()
            logger.info("fetchAndSaveMonthlyMeals 수행 시간: ${(end - start) / 1000.0}초")
        }
    }

    suspend fun fetchAndSaveMonthlyMeals() = coroutineScope {
        val now = LocalDate.now()
        val daysInMonth = now.lengthOfMonth()

        logger.info("급식 데이터 수집 시작: ${now.year}년 ${now.monthValue}월 (${daysInMonth}일)")

        // 세마포어를 사용한 동시 실행 수 제한
        val semaphore = kotlinx.coroutines.sync.Semaphore(CONCURRENT_REQUESTS)

        val allMeals = (1..daysInMonth).map { day ->
            async(Dispatchers.IO) {
                semaphore.withPermit {
                    val date = now.withDayOfMonth(day)
                    fetchDayMealData(date)
                }
            }
        }.awaitAll().flatten()

        if (allMeals.isNotEmpty()) {
            transactionService.saveMealsInBulk(allMeals)
            logger.info("급식 데이터 ${allMeals.size}건 저장 완료")
        } else {
            logger.warn("수집된 급식 데이터가 없습니다.")
        }
    }

    private suspend fun fetchDayMealData(date: LocalDate): List<MealInfo> {
        return try {
            val dateStr = date.format(DATE_FORMATTER)
            val url = buildApiUrl(dateStr, dateStr)

            logger.debug("급식 데이터 요청: {}", date)

            val json = webClient.get()
                .uri(url)
                .retrieve()
                .awaitBody<String>()

            // CPU 작업(파싱)
            withContext(Dispatchers.Default) {
                parseMealData(json)
            }.also {
                if (it.isNotEmpty()) {
                    logger.debug("{} 급식 데이터 {}건 파싱 완료", date, it.size)
                }
            }
        } catch (e: Exception) {
            logger.warn("$date 급식 데이터 조회 실패: ${e.message}")
            emptyList()
        }
    }

    private fun buildApiUrl(from: String, to: String): String {
        val OFFICE_CODE = "D10"
        val SCHOOL_CODE = "7240454"
        val PAGE_SIZE = 10
        return "https://open.neis.go.kr/hub/mealServiceDietInfo" +
                "?ATPT_OFCDC_SC_CODE=$OFFICE_CODE&SD_SCHUL_CODE=$SCHOOL_CODE" +
                "&KEY=$apiKey&MLSV_FROM_YMD=$from&MLSV_TO_YMD=$to" +
                "&Type=json&pSize=$PAGE_SIZE"
    }

    private fun parseMealData(json: String): List<MealInfo> {
        val root = objectMapper.readTree(json)
        val rows = root.path("mealServiceDietInfo")
            .path(API_RESPONSE_DATA_INDEX)
            .path("row")

        if (rows.isMissingNode || !rows.isArray) {
            return emptyList()
        }

        val allergyPattern = "\\((\\d+(?:\\.\\d+)*)\\)".toRegex()

        return rows.mapNotNull { row ->
            val mealDate = row.path("MLSV_YMD").asText()
            val menuWithAllergiesRaw = row.path("DDISH_NM").asText()
            val calories = row.path("CAL_INFO").asText()
                .replace("Kcal", "")
                .trim()
                .toFloatOrNull() ?: 0f

            val mealTypeCode = row.path("MMEAL_SC_CODE").asText().toIntOrNull()
            val mealType = MealType.entries.find { it.code == mealTypeCode }

            if (mealType == null) {
                logger.warn("알 수 없는 급식 타입: $mealTypeCode")
                return@mapNotNull null
            }

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