package com.example.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class Task(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val title: String,
    val description: String = "",
    val priority: String, // "HIGH", "MEDIUM", "LOW"
    val colorHex: String, // Hex code representing task accent color
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)
