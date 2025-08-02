package com.example.expresscodeassistant.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "regex_rules")
data class RegexRule(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val name: String,
    val companyPrefix: String? = null,
    val companySuffix: String? = null,
    val phonePrefix: String? = null,
    val phoneSuffix: String? = null,
    val codePrefix: String? = null,
    val codeSuffix: String? = null,
    val addressPrefix: String? = null,
    val addressSuffix: String? = null,
    val stationPrefix: String? = null,
    val stationSuffix: String? = null,
    val isDefault: Boolean = false
)