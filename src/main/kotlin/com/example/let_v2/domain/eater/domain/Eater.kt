package com.example.let_v2.domain.eater.domain

data class Eater(
    val id : Long?=null,
    val userId : Long,
    val mealId : Int,
    val eaten : Boolean
)
