package com.yakbas.batch.entity

import org.springframework.data.annotation.Id
import org.springframework.data.relational.core.mapping.Table

@Table(User.TABLE_NAME)
data class User(
    @Id val id: Long?,
    val firstName: String,
    val lastName: String,
    var email: String,
    val fullName: String? // later on we will set this column with batching. Therefore, it is null now.
) {
    companion object {
        const val TABLE_NAME = "users"
    }
}
