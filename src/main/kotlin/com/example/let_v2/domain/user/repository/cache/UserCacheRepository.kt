package com.example.let_v2.domain.user.repository.cache

import com.example.let_v2.domain.user.domain.User
import com.fasterxml.jackson.databind.ObjectMapper
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Repository
import java.util.concurrent.TimeUnit

@Repository
class UserCacheRepository(
    private val redisTemplate: RedisTemplate<String, String>,
    private val objectMapper: ObjectMapper
) {
    companion object{
        private const val KEY_PREFIX = "user:"
        private const val TTL_MINUTES = 30L
    }

    fun save(user: User){
        val key = getKey(user.name)
        val value = objectMapper.writeValueAsString(user)
        redisTemplate.opsForValue().set(key,value ,TTL_MINUTES, TimeUnit.MINUTES)
    }

    fun findByUsername(username: String): User? {
        val key = getKey(username)
        val value = redisTemplate.opsForValue().get(key) ?: return null
        return objectMapper.readValue(value, User::class.java)
    }

    fun delete(username: String) {
        redisTemplate.delete(getKey(username))
    }


    private fun getKey(username: String): String = KEY_PREFIX + username

}