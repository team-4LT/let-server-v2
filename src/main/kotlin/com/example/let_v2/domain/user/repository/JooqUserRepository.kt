package com.example.let_v2.domain.user.repository

import com.example.auth.generated.jooq.enums.UsersRole
import com.example.auth.generated.jooq.tables.Users
import com.example.auth.generated.jooq.tables.records.UsersRecord
import com.example.let_v2.domain.user.domain.User
import com.example.let_v2.domain.user.domain.UserRole
import org.jooq.DSLContext
import org.springframework.stereotype.Repository

@Repository
class JooqUserRepository(private val dsl: DSLContext) : UserRepository {
    override fun save(user: User): User {
        val record = dsl.insertInto(Users.Companion.USERS)
            .set(Users.Companion.USERS.USERNAME, user.name)
            .set(Users.Companion.USERS.PASSWORD, user.password)
            .set(Users.Companion.USERS.ROLE, UsersRole.valueOf(user.role.name))
            .set(Users.Companion.USERS.STUDENT_ID, user.studentId)
            .set(Users.Companion.USERS.REAL_NAME, user.realName)
            .returning()
            .fetchOne()!!

        return record.toDomain()
    }

    override fun findByName(name: String): User? {
        return dsl.selectFrom(Users.Companion.USERS)
            .where(Users.Companion.USERS.USERNAME.eq(name))
            .fetchOne()
            ?.toDomain()
    }

    override fun existsByName(name: String): Boolean {
        return dsl.fetchExists(
            dsl.selectFrom(Users.Companion.USERS)
                .where(Users.Companion.USERS.USERNAME.eq(name))
        )
    }


    private fun UsersRecord.toDomain(): User {
        return User(
            id = this.userId,
            name = requireNotNull(this.username),
            password = requireNotNull(this.password),
            role = this.role?.name?.let { db ->
                UserRole.entries.find { it.name == db } ?: UserRole.STUDENT
            } ?: UserRole.STUDENT,
            studentId = requireNotNull(this.studentId),
            realName = requireNotNull(this.realName)
        )
    }
}