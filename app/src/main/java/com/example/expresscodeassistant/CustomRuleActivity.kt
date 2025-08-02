package com.example.expresscodeassistant

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import com.example.expresscodeassistant.database.AppDatabase
import com.example.expresscodeassistant.databinding.ActivityCustomRuleBinding
import com.example.expresscodeassistant.model.RegexRule
import kotlinx.coroutines.launch

class CustomRuleActivity : AppCompatActivity() {
    private lateinit var binding: ActivityCustomRuleBinding
    private lateinit var db: AppDatabase

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCustomRuleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化数据库
        db = AppDatabase.getDatabase(this)

        // 设置工具栏
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.title_custom_rule)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)

        // 设置文本变化监听，实时解析短信
        binding.etTestSms.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                parseSms()
            }

            override fun afterTextChanged(s: Editable?) {}
        })

        // 设置按钮点击事件
        binding.btnReset.setOnClickListener {
            resetFields()
        }

        binding.btnSave.setOnClickListener {
            saveCustomRule()
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun resetFields() {
        binding.etLabelPrefix.text.clear()
        binding.etLabelSuffix.text.clear()
        binding.etPhonePrefix.text.clear()
        binding.etPhoneSuffix.text.clear()
        binding.etCodePrefix.text.clear()
        binding.etCodeSuffix.text.clear()
        binding.etAddressPrefix.text.clear()
        binding.etAddressSuffix.text.clear()
        binding.etTestSms.text.clear()
        binding.tvLabelResult.text = ""
        binding.tvPhoneResult.text = ""
        binding.tvCodeResult.text = ""
        binding.tvAddressResult.text = ""
    }

    private fun parseSms() {
        val smsContent = binding.etTestSms.text.toString()
        if (smsContent.isEmpty()) {
            clearResults()
            return
        }

        // 提取各字段信息
        val label = extractInfo(smsContent, binding.etLabelPrefix.text.toString(), binding.etLabelSuffix.text.toString())
        val phone = extractInfo(smsContent, binding.etPhonePrefix.text.toString(), binding.etPhoneSuffix.text.toString())
        val code = extractInfo(smsContent, binding.etCodePrefix.text.toString(), binding.etCodeSuffix.text.toString())
        val address = extractInfo(smsContent, binding.etAddressPrefix.text.toString(), binding.etAddressSuffix.text.toString())

        // 显示结果
        binding.tvLabelResult.text = label ?: "未找到"
        binding.tvPhoneResult.text = phone ?: "未找到"
        binding.tvCodeResult.text = code ?: "未找到"
        binding.tvAddressResult.text = address ?: "未找到"
    }

    private fun extractInfo(content: String, prefix: String, suffix: String): String? {
        if (prefix.isEmpty() && suffix.isEmpty()) {
            return null
        }

        val regexBuilder = StringBuilder()
        if (prefix.isNotEmpty()) {
            regexBuilder.append(Regex.escape(prefix))
        }
        regexBuilder.append("(.*?)")
        if (suffix.isNotEmpty()) {
            regexBuilder.append(Regex.escape(suffix))
        }

        val regex = Regex(regexBuilder.toString())
        val matchResult = regex.find(content)
        return matchResult?.groupValues?.get(1)?.trim()
    }

    private fun clearResults() {
        binding.tvLabelResult.text = ""
        binding.tvPhoneResult.text = ""
        binding.tvCodeResult.text = ""
        binding.tvAddressResult.text = ""
    }

    private fun saveCustomRule() {
        val labelPrefix = binding.etLabelPrefix.text.toString()
        val labelSuffix = binding.etLabelSuffix.text.toString()
        val phonePrefix = binding.etPhonePrefix.text.toString()
        val phoneSuffix = binding.etPhoneSuffix.text.toString()
        val codePrefix = binding.etCodePrefix.text.toString()
        val codeSuffix = binding.etCodeSuffix.text.toString()
        val addressPrefix = binding.etAddressPrefix.text.toString()
        val addressSuffix = binding.etAddressSuffix.text.toString()

        // 验证至少有一个字段有设置
        if (labelPrefix.isEmpty() && labelSuffix.isEmpty() &&
            phonePrefix.isEmpty() && phoneSuffix.isEmpty() &&
            codePrefix.isEmpty() && codeSuffix.isEmpty() &&
            addressPrefix.isEmpty() && addressSuffix.isEmpty()
        ) {
            Toast.makeText(this, "至少需要设置一个字段的前缀或后缀", Toast.LENGTH_SHORT).show()
            return
        }

        // 保存自定义规则
        lifecycleScope.launch {
            val ruleName = if (labelPrefix.isNotEmpty() || labelSuffix.isNotEmpty()) {
                "自定义规则_${labelPrefix}${labelSuffix}"
            } else {
                "自定义规则_${System.currentTimeMillis()}"
            }

            val newRule = RegexRule(
                name = ruleName,
                companyPrefix = labelPrefix,
                companySuffix = labelSuffix,
                phonePrefix = phonePrefix,
                phoneSuffix = phoneSuffix,
                codePrefix = codePrefix,
                codeSuffix = codeSuffix,
                addressPrefix = addressPrefix,
                addressSuffix = addressSuffix,
                isDefault = false
            )

            db.expressDao().insertRegexRule(newRule)
            Toast.makeText(this@CustomRuleActivity, "自定义规则已保存", Toast.LENGTH_SHORT).show()
            finish()
        }
    }
}