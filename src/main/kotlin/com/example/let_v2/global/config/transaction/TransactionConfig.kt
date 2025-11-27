package com.example.let_v2.global.config.transaction

import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.transaction.PlatformTransactionManager
import org.springframework.transaction.support.TransactionTemplate

@Configuration
class TransactionConfig {
    @Bean
    fun transactionTemplate(
        transactionManager: PlatformTransactionManager
    ): TransactionTemplate {
        return TransactionTemplate(transactionManager)
    }
}