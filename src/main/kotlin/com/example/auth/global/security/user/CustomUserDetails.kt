package com.example.auth.global.security.user

import com.example.auth.domain.user.domain.User
import org.springframework.security.core.GrantedAuthority
import org.springframework.security.core.authority.SimpleGrantedAuthority
import org.springframework.security.core.userdetails.UserDetails

class CustomUserDetails(
    private val user: User
): UserDetails {
    override fun getAuthorities(): Collection<GrantedAuthority?>? {
        return listOf(SimpleGrantedAuthority("ROLE_${user.role.name}"))
    }

    override fun getPassword(): String? {
        return user.password
    }

    override fun getUsername(): String? {
        return user.name
    }

    override fun isAccountNonExpired(): Boolean = true

    override fun isAccountNonLocked(): Boolean = true

    override fun isCredentialsNonExpired(): Boolean = true

    override fun isEnabled(): Boolean = true
}