package com.example.let_v2.domain.mealmenu.service

import com.example.let_v2.domain.allergy.Allergy
import com.example.let_v2.domain.meal.domain.Meal
import com.example.let_v2.domain.meal.dto.MealInfo
import com.example.let_v2.domain.meal.repository.MealRepository
import com.example.let_v2.domain.mealmenu.domain.MealMenu
import com.example.let_v2.domain.mealmenu.repository.MealMenuRepository
import com.example.let_v2.domain.menu.domain.Menu
import com.example.let_v2.domain.menu.domain.MenuAllergy
import com.example.let_v2.domain.menu.repository.MenuRepository
import com.example.let_v2.domain.menu.repository.allergy.MenuAllergyRepository
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Service
class MealDataPersistenceService(
    private val mealRepository: MealRepository,
    private val menuRepository: MenuRepository,
    private val mealMenuRepository: MealMenuRepository,
    private val menuAllergyRepository: MenuAllergyRepository,
) {
    private val DATE_FORMATTER = DateTimeFormatter.ofPattern("yyyyMMdd")

    @Transactional
    fun saveMealsInBulk(mealInfos: List<MealInfo>) {
        if (mealInfos.isEmpty()) return

        // 1. Meal upsert (날짜+타입 조합으로 중복 체크)
        val mealsToSave = mealInfos.map {
            Meal(
                mealDate = LocalDate.parse(it.mealDate, DATE_FORMATTER),
                mealType = it.mealType,
                calories = it.calories
            )
        }

        // 기존 Meal 조회
        val mealDates = mealsToSave.map { it.mealDate }.distinct()
        val existingMeals = mealRepository.findAllByMealDateIn(mealDates)
            .associateBy { it.mealDate to it.mealType }

        // 새로운 Meal만 저장
        val newMeals = mealsToSave.filter {
            (it.mealDate to it.mealType) !in existingMeals.keys
        }

        val savedNewMeals = if (newMeals.isNotEmpty()) {
            mealRepository.saveAll(newMeals)
        } else {
            emptyList()
        }

        val allMeals = (existingMeals.values + savedNewMeals)
            .associateBy { it.mealDate to it.mealType }

        // 2. Menu 저장 (기존 로직 유지)
        val allMenuNames = mealInfos.flatMap { it.menus.map { menu -> menu.menuName } }.distinct()
        val existingMenus = menuRepository.findAllByNameIn(allMenuNames).associateBy { it.name }
        val newMenuNames = allMenuNames.filter { it !in existingMenus.keys }
        val newMenus = if (newMenuNames.isNotEmpty()) {
            menuRepository.saveAll(newMenuNames.map { Menu(name = it) })
        } else {
            emptyList()
        }
        val allMenus = (existingMenus.values + newMenus).associateBy { it.name }

        // 3. 관계 데이터 생성 (중복 체크 추가)
        val mealMenusToSave = mutableSetOf<Pair<Long, Long>>() // mealId, menuId 쌍
        val menuAllergiesToSave = mutableSetOf<Pair<Long, Allergy>>() // menuId, allergy 쌍

        mealInfos.forEach { mealInfo ->
            val mealDate = LocalDate.parse(mealInfo.mealDate, DATE_FORMATTER)
            val savedMeal = allMeals[mealDate to mealInfo.mealType] ?: return@forEach
            val mealId = savedMeal.id ?: return@forEach

            mealInfo.menus.forEach { menuInfo ->
                val menu = allMenus[menuInfo.menuName] ?: return@forEach
                val menuId = menu.id ?: return@forEach

                mealMenusToSave.add(mealId.toLong() to menuId)

                menuInfo.allergies.forEach { allergyId ->
                    Allergy.fromId(allergyId)?.let { allergy ->
                        menuAllergiesToSave.add(menuId to allergy)
                    }
                }
            }
        }

        // 4. 기존 관계 데이터 조회 후 새것만 저장
        if (mealMenusToSave.isNotEmpty()) {
            val existingMealMenus = mealMenuRepository.findAllByMealIdIn(
                mealMenusToSave.map { it.first }.distinct()
            ).map { it.mealId to it.menuId }.toSet()

            val newMealMenus = mealMenusToSave
                .filter { pair -> existingMealMenus.none { it.first.toLong() == pair.first && it.second == pair.second } }
                .map { MealMenu(mealId = it.first.toInt(), menuId = it.second) }

            if (newMealMenus.isNotEmpty()) {
                mealMenuRepository.saveAllBatch(newMealMenus)
            }
        }

        if (menuAllergiesToSave.isNotEmpty()) {
            val existingMenuAllergies = menuAllergyRepository.findAllByMenuIdIn(
                menuAllergiesToSave.map { it.first }.distinct()
            ).map { it.menuId to it.allergy }.toSet()

            val newMenuAllergies = menuAllergiesToSave
                .filter { it !in existingMenuAllergies }
                .map { MenuAllergy(menuId = it.first, allergy = it.second) }

            if (newMenuAllergies.isNotEmpty()) {
                menuAllergyRepository.saveAllBatch(newMenuAllergies)
            }
        }
    }
}