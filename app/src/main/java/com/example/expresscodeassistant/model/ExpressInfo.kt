package com.example.expresscodeassistant.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.util.Date

@Entity(tableName = "express_info")
data class ExpressInfo(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val company: String,
    val code: String,
    val station: String,
    val address: String,
    val phone: String? = null,
    val message: String,
    val timestamp: Date,
    var isPicked: Boolean = false
)