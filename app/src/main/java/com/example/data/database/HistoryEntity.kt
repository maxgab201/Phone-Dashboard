package com.example.data.database

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "system_history")
data class HistoryEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val cpuUsage: Float,
    val ramUsedPercent: Float,
    val batteryLevel: Int,
    val batteryTemp: Float,
    val storageUsedPercent: Float
)
