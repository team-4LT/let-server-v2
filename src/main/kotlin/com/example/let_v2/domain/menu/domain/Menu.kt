package com.example.let_v2.domain.menu.domain

data class Menu (
    val id : Long?= null,
    val name : String,
    val score : Double?= 0.0,
    val likeCount : Long?= 0,
    val dislikeCount : Long ?= 0,
    val currentRank : Int ?= 0,
)