package com.example.expresscodeassistant.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.telephony.SmsMessage
import android.util.Log
import com.example.expresscodeassistant.database.AppDatabase
import com.example.expresscodeassistant.model.ExpressInfo
import com.example.expresscodeassistant.model.RegexRule
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Date

class SmsReceiver : BroadcastReceiver() {
    private val TAG = "SmsReceiver"

    override fun onReceive(context: Context, intent: Intent) {
        val bundle: Bundle? = intent.extras
        if (bundle != null) {
            val pdusObj = bundle.get("pdus") as Array<*>
            for (i in pdusObj.indices) {
                val smsMessage = SmsMessage.createFromPdu(pdusObj[i] as ByteArray)
                val messageBody = smsMessage.messageBody
                val sender = smsMessage.displayOriginatingAddress
                Log.d(TAG, "Received SMS: $messageBody from $sender")

                // 在后台线程中处理短信
                CoroutineScope(Dispatchers.IO).launch {
                    processSms(context, messageBody)
                }
            }
        }
    }

    private suspend fun processSms(context: Context, messageBody: String) {
        val db = AppDatabase.getDatabase(context)
        val dao = db.expressDao()

        // 获取默认正则规则
        val defaultRule = dao.getDefaultRegexRule()
        if (defaultRule != null) {
            // 使用正则规则解析短信
            val expressInfo = parseExpressInfo(messageBody, defaultRule)
            if (expressInfo != null) {
                // 保存到数据库
                dao.insertExpressInfo(expressInfo)
                Log.d(TAG, "Saved express info: $expressInfo")
            }
        } else {
            Log.d(TAG, "No default regex rule found")
        }
    }

    private fun parseExpressInfo(message: String, rule: RegexRule): ExpressInfo? {
        // 这里实现正则解析逻辑
        // 简化版本，实际应用中需要更复杂的正则处理
        val company = extractInfo(message, rule.companyPrefix, rule.companySuffix)
        val code = extractInfo(message, rule.codePrefix, rule.codeSuffix)
        val station = extractInfo(message, rule.stationPrefix, rule.stationSuffix)
        val address = extractInfo(message, rule.addressPrefix, rule.addressSuffix)
        val phone = extractInfo(message, rule.phonePrefix, rule.phoneSuffix)

        // 如果取件码为空，则不保存
        if (code.isNullOrEmpty()) {
            return null
        }

        return ExpressInfo(
            company = company ?: "未知",
            code = code,
            station = station ?: "未知",
            address = address ?: "未知",
            phone = phone,
            message = message,
            timestamp = Date()
        )
    }

    private fun extractInfo(message: String, prefix: String?, suffix: String?): String? {
        if (prefix.isNullOrEmpty() && suffix.isNullOrEmpty()) {
            return null
        }

        val regexPattern = StringBuilder()
        if (!prefix.isNullOrEmpty()) {
            regexPattern.append(Regex.escape(prefix))
        }
        regexPattern.append("(.*?)")
        if (!suffix.isNullOrEmpty()) {
            regexPattern.append(Regex.escape(suffix))
        }

        val regex = Regex(regexPattern.toString())
        val matchResult = regex.find(message)
        return matchResult?.groupValues?.get(1)?.trim()
    }
}