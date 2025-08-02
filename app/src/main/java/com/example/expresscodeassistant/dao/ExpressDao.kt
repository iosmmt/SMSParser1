package com.example.expresscodeassistant.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.expresscodeassistant.model.ExpressInfo
import com.example.expresscodeassistant.model.RegexRule
import kotlinx.coroutines.flow.Flow

@Dao
interface ExpressDao {
    // ExpressInfo operations
    @Insert
    suspend fun insertExpressInfo(expressInfo: ExpressInfo)

    @Update
    suspend fun updateExpressInfo(expressInfo: ExpressInfo)

    @Delete
    suspend fun deleteExpressInfo(expressInfo: ExpressInfo)

    @Query("SELECT * FROM express_info ORDER BY timestamp DESC")
    fun getAllExpressInfo(): Flow<List<ExpressInfo>>

    @Query("SELECT * FROM express_info WHERE isPicked = 0 ORDER BY timestamp DESC")
    fun getUnpickedExpressInfo(): Flow<List<ExpressInfo>>

    @Query("SELECT * FROM express_info WHERE isPicked = 1 ORDER BY timestamp DESC")
    fun getPickedExpressInfo(): Flow<List<ExpressInfo>>

    // RegexRule operations
    @Insert
    suspend fun insertRegexRule(regexRule: RegexRule)

    @Update
    suspend fun updateRegexRule(regexRule: RegexRule)

    @Delete
    suspend fun deleteRegexRule(regexRule: RegexRule)

    @Query("SELECT * FROM regex_rules ORDER BY id ASC")
    fun getAllRegexRules(): Flow<List<RegexRule>>

    @Query("SELECT * FROM regex_rules WHERE isDefault = 1")
    suspend fun getDefaultRegexRule(): RegexRule?
}