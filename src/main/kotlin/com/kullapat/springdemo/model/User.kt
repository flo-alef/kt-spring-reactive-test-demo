package com.kullapat.springdemo.model

import org.springframework.data.mongodb.core.mapping.Document

@Document(collection = "users")
data class User(
    val id: String?,
    val firstName: String,
    val lastName: String
)