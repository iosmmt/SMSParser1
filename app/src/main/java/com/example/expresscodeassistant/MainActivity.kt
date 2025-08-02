package com.example.expresscodeassistant

import android.Manifest
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.expresscodeassistant.adapter.ExpressAdapter
import com.example.expresscodeassistant.database.AppDatabase
import com.example.expresscodeassistant.databinding.ActivityMainBinding
import com.example.expresscodeassistant.model.ExpressInfo
import com.example.expresscodeassistant.model.RegexRule
import kotlinx.coroutines.launch

class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private lateinit var adapter: ExpressAdapter
    private lateinit var db: AppDatabase
    private val SMS_PERMISSION_CODE = 100

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 初始化数据库
        db = AppDatabase.getDatabase(this)

        // 设置工具栏
        setSupportActionBar(binding.toolbar)
        supportActionBar?.title = getString(R.string.app_name)

        // 检查短信权限
        checkSmsPermission()

        // 初始化RecyclerView
        initRecyclerView()

        // 设置按钮点击事件
        binding.btnRuleSetting.setOnClickListener {
            startActivity(Intent(this, RuleSettingActivity::class.java))
        }

        binding.btnCustomRule.setOnClickListener {
            startActivity(Intent(this, CustomRuleActivity::class.java))
        }

        // 检查是否有默认规则，如果没有则创建
        checkDefaultRule()
    }

    private fun checkSmsPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_SMS
            ) != PackageManager.PERMISSION_GRANTED ||
            ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.RECEIVE_SMS
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            // 请求权限
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.READ_SMS, Manifest.permission.RECEIVE_SMS),
                SMS_PERMISSION_CODE
            )
        } else {
            // 权限已授予，加载快递信息
            loadExpressInfo()
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == SMS_PERMISSION_CODE) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // 权限授予成功
                loadExpressInfo()
            } else {
                // 权限授予失败
                Toast.makeText(
                    this,
                    "需要短信权限才能使用取件码助手功能",
                    Toast.LENGTH_LONG
                ).show()
                binding.tvEmpty.visibility = android.view.View.VISIBLE
            }
        }
    }

    private fun initRecyclerView() {
        adapter = ExpressAdapter(
            onItemClick = { expressInfo ->
                // 标记为已取
                markAsPicked(expressInfo)
            }
        )

        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        binding.recyclerView.adapter = adapter

        // 添加左滑删除功能
        val itemTouchHelper = ItemTouchHelper(object : ItemTouchHelper.SimpleCallback(
            0, ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT
        ) {
            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val expressInfo = adapter.currentList[position]
                deleteExpressInfo(expressInfo)
            }
        })
        itemTouchHelper.attachToRecyclerView(binding.recyclerView)
    }

    private fun loadExpressInfo() {
        lifecycleScope.launch {
            db.expressDao().getUnpickedExpressInfo().collect {
                adapter.submitList(it)
                binding.tvEmpty.visibility = if (it.isEmpty()) android.view.View.VISIBLE else android.view.View.GONE
            }
        }
    }

    private fun markAsPicked(expressInfo: ExpressInfo) {
        lifecycleScope.launch {
            val updatedExpressInfo = expressInfo.copy(isPicked = true)
            db.expressDao().updateExpressInfo(updatedExpressInfo)
            Toast.makeText(this@MainActivity, "已标记为已取", Toast.LENGTH_SHORT).show()
        }
    }

    private fun deleteExpressInfo(expressInfo: ExpressInfo) {
        lifecycleScope.launch {
            db.expressDao().deleteExpressInfo(expressInfo)
            Toast.makeText(this@MainActivity, "已删除", Toast.LENGTH_SHORT).show()
        }
    }

    private fun checkDefaultRule() {
        lifecycleScope.launch {
            val defaultRule = db.expressDao().getDefaultRegexRule()
            if (defaultRule == null) {
                // 创建默认规则
                val rule = RegexRule(
                    name = "默认规则",
                    companyPrefix = "【",
                    companySuffix = "】",
                    codePrefix = "取件码",
                    codeSuffix = "\n",
                    stationPrefix = "@",
                    stationSuffix = "\n",
                    addressPrefix = "地址：",
                    addressSuffix = "\n",
                    isDefault = true
                )
                db.expressDao().insertRegexRule(rule)
            }
        }
    }
}