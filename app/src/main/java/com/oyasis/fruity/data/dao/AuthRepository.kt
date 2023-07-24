package com.oyasis.fruity.data.dao

import com.oyasis.fruity.data.dto.LoginRequest
import com.oyasis.fruity.data.dto.LoginResponse
import com.oyasis.fruity.data.dto.SignupRequest
import com.oyasis.fruity.data.dto.SignupResponse
import com.oyasis.fruity.data.model.User

interface AuthRepository {
    suspend fun login(req: LoginRequest) : LoginResponse?
    suspend fun logout()
    suspend fun register(req: SignupRequest): SignupResponse?
    suspend fun loggedInUser(): User?
}