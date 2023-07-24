package com.oyasis.fruity.data.dto

import com.oyasis.fruity.data.model.User


data class LoginResponse(
        var userId: String,
        var emailAddress: String,
        var firstName: String,
        var lastName: String,
        var role: String,
        var telephone: String,
    )


fun LoginResponse?.mapToUser(): User? {
    if (this == null) {
        return null
    }
    return User(userId, emailAddress, firstName, lastName, role, telephone)
}