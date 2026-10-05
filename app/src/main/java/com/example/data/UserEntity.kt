package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "users")
data class UserEntity(
    @PrimaryKey
    val id: String, // Employee ID
    val name: String,
    val password: String,
    val department: String = "Operations & Production",
    val designation: String = "Staff Member",
    val createdAt: Long = System.currentTimeMillis()
)
