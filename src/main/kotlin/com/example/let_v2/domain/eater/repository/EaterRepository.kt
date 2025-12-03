package com.example.let_v2.domain.eater.repository

import com.example.let_v2.domain.eater.domain.Eater

interface EaterRepository {
    fun saveAll(eaters: List<Eater>)
}