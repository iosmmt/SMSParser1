package com.example.expresscodeassistant

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expresscodeassistant.database.AppDatabase
import com.example.expresscodeassistant.databinding.ActivityRuleSettingBinding
import com.example.expresscodeassistant.model.RegexRule
import kotlinx.coroutines.launch

class RuleSettingActivity : AppCompatActivity() {
    private lateinit var binding: ActivityRuleSettingBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityRuleSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化数据库
        db = AppDatabase.getDatabase(this)

        // 设置工具栏
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.title_rule_setting)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 加载默认规则
        loadDefaultRule()

        // 设置按钮点击事件
        binding.btnReset.setOnClickListener {
            resetRules()
        }

        binding.btnSave.setOnClickListener {
            saveRules()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun loadDefaultRule() {
        lifecycleScope.launch {
            val defaultRule = db.expressDao().getDefaultRegexRule()
            defaultRule?.let {
                binding.etCompanyRegex.setText(it.companyPrefix + "(.*?)" + it.companySuffix)
                binding.etCodeRegex.setText(it.codePrefix + "(.*?)" + it.codeSuffix)
                binding.etStationRegex.setText(it.stationPrefix + "(.*?)" + it.stationSuffix)
                binding.etAddressRegex.setText(it.addressPrefix + "(.*?)" + it.addressSuffix)
                binding.etPhoneRegex.setText(it.phonePrefix + "(.*?)" + it.phoneSuffix)
            }
        }
    }

    private fun resetRules() {
        binding.etCompanyRegex.setText("【(.*?)】")
        binding.etCodeRegex.setText("取件码(.*?)\n")
        binding.etStationRegex.setText("@(.*?)\n")
        binding.etAddressRegex.setText("地址：(.*?)\n")
        binding.etPhoneRegex.setText("")
    }

    private fun saveRules() {
        val companyRegex = binding.etCompanyRegex.text.toString()
        val codeRegex = binding.etCodeRegex.text.toString()
        val stationRegex = binding.etStationRegex.text.toString()
        val addressRegex = binding.etAddressRegex.text.toString()
        val phoneRegex = binding.etPhoneRegex.text.toString()

        // 解析正则表达式，提取前缀和后缀
        val companyPrefix = extractPrefix(companyRegex)
        val companySuffix = extractSuffix(companyRegex)
        val codePrefix = extractPrefix(codeRegex)
        val codeSuffix = extractSuffix(codeRegex)
        val stationPrefix = extractPrefix(stationRegex)
        val stationSuffix = extractSuffix(stationRegex)
        val addressPrefix = extractPrefix(addressRegex)
        val addressSuffix = extractSuffix(addressRegex)
        val phonePrefix = extractPrefix(phoneRegex)
        val phoneSuffix = extractSuffix(phoneRegex)

        // 保存规则
        lifecycleScope.launch {
            val defaultRule = db.expressDao().getDefaultRegexRule()
            if (defaultRule != null) {
                val updatedRule = defaultRule.copy(
                    companyPrefix = companyPrefix,
                    companySuffix = companySuffix,
                    codePrefix = codePrefix,
                    codeSuffix = codeSuffix,
                    stationPrefix = stationPrefix,
                    stationSuffix = stationSuffix,
                    addressPrefix = addressPrefix,
                    addressSuffix = addressSuffix,
                    phonePrefix = phonePrefix,
                    phoneSuffix = phoneSuffix
                )
                db.expressDao().updateRegexRule(updatedRule)
            } else {
                val newRule = RegexRule(
                    name = "默认规则",
                    companyPrefix = companyPrefix,
                    companySuffix = companySuffix,
                    codePrefix = codePrefix,
                    codeSuffix = codeSuffix,
                    stationPrefix = stationPrefix,
                    stationSuffix = stationSuffix,
                    addressPrefix = addressPrefix,
                    addressSuffix = addressSuffix,
                    phonePrefix = phonePrefix,
                    phoneSuffix = phoneSuffix,
                    isDefault = true
                )
                db.expressDao().insertRegexRule(newRule)
            }
            Toast.makeText(this@RuleSettingActivity, "规则已保存", Toast.LENGTH_SHORT).show()
            finish()
        }
    }

    private fun extractPrefix(regex: String): String? {
        val startIndex = regex.indexOf("(.*?)")
        return if (startIndex > 0) regex.substring(0, startIndex) else null
    }

    private fun extractSuffix(regex: String): String? {
        val endIndex = regex.indexOf("(.*?)") + 5 // "(.*?)" 长度为5
        return if (endIndex < regex.length) regex.substring(endIndex) else null
    }
}