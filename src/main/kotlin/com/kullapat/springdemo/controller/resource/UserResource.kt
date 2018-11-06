package com.kullapat.springdemo.controller.resource

import com.kullapat.springdemo.model.User

data class UserResource(
    val firstName: String,
    val lastName: String
) {
    fun toUser(): User {
        return User(id = null, firstName = firstName, lastName = lastName)
    }
}
