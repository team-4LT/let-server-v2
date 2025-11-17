package com.example.let_v2.domain.auth.repository

import com.example.let_v2.domain.auth.domain.BlacklistToken
import org.springframework.data.repository.CrudRepository

interface BlacklistTokenRepository : CrudRepository<BlacklistToken, String>

