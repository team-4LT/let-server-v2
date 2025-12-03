package com.example.let_v2

import com.example.let_v2.global.config.properties.SecurityProperties
import org.springframework.boot.autoconfigure.SpringBootApplication
import org.springframework.boot.context.properties.EnableConfigurationProperties
import org.springframework.boot.runApplication

@SpringBootApplication
@EnableConfigurationProperties(SecurityProperties::class)
class AuthApplication
fun main(args: Array<String>) {
    runApplication<AuthApplication>(*args)
}
