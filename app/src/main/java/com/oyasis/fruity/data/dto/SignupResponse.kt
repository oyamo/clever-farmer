package com.oyasis.fruity.data.dto

import com.oyasis.fruity.data.model.User

data class SignupResponse(
        var userId: String,
        var emailAddress: String,
        var firstName: String,
        var lastName: String,
        var role: String,
        var telephone: String,
)


fun SignupResponse?.mapToUser(): User? {
    if(this == null) return null
    return User(
            userId,
            emailAddress,
            firstName,
            lastName,
            role,
            telephone
    )
}





