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
        val record = dsl.insertInto(Users.USERS)
            .set(Users.USERS.USERNAME, user.name)
            .set(Users.USERS.PASSWORD, user.password)
            .set(Users.USERS.ROLE, UsersRole.valueOf(user.role.name))
            .set(Users.USERS.STUDENT_ID, user.studentId)
            .set(Users.USERS.REAL_NAME, user.realName)
            .returning()
            .fetchOne()!!

        return record.toDomain()
    }

    override fun findByName(name: String): User? {
        return dsl.selectFrom(Users.USERS)
            .where(Users.USERS.USERNAME.eq(name))
            .fetchOne()
            ?.toDomain()
    }

    override fun existsByName(name: String): Boolean {
        return dsl.fetchExists(
            dsl.selectFrom(Users.USERS)
                .where(Users.USERS.USERNAME.eq(name))
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