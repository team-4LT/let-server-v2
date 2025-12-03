package com.example.let_v2.domain.eater.service

import com.example.let_v2.domain.eater.domain.Eater
import com.example.let_v2.domain.eater.dto.response.EaterResponse
import com.example.let_v2.domain.eater.error.EaterError
import com.example.let_v2.domain.eater.repository.EaterRepository
import com.example.let_v2.domain.meal.repository.MealRepository
import com.example.let_v2.domain.meal.repository.getCurrentMeal
import com.example.let_v2.domain.user.dto.response.UserEaterResponse
import com.example.let_v2.domain.user.repository.UserRepository
import com.example.let_v2.domain.user.repository.findByIdOrThrow
import com.example.let_v2.global.error.CustomException
import org.springframework.scheduling.annotation.Scheduled
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional

@Service
@Transactional
class EaterService(
    private val eaterRepository: EaterRepository,
    private val userRepository: UserRepository,
    private val mealRepository: MealRepository
) {
    @Scheduled(cron = "0 0 0,10,15 * * *")
    fun insertEater(){
        try {
            val meal = mealRepository.getCurrentMeal()
            val users = userRepository.findAllStudents()
            val eaters = users.map { user ->
                Eater(
                    userId = user.id!!,
                    mealId = meal.id!!,
                    eaten = false
                )
            }
            eaterRepository.saveAll(eaters)
        } catch (e: Exception) {
        }
    }

    fun findByGrade(grade: Int): List<EaterResponse>{
        if (grade < 1 || grade > 3){
            throw CustomException(EaterError.INVALID_GRADE)
        }

        return eaterRepository.findByGrade(grade).map { eater ->
            val user = userRepository.findByIdOrThrow(eater.userId)
            val of = EaterResponse.of(eater, UserEaterResponse.of(user))
            of
        }
    }
}