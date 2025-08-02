package com.example.expresscodeassistant.widget

import android.app.PendingIntent
import android.appwidget.AppWidgetManager
import android.appwidget.AppWidgetProvider
import android.content.Context
import android.content.Intent
import android.widget.RemoteViews
import com.example.expresscodeassistant.MainActivity
import com.example.expresscodeassistant.R
import com.example.expresscodeassistant.database.AppDatabase
import com.example.expresscodeassistant.model.ExpressInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class ExpressWidgetProvider : AppWidgetProvider() {
    override fun onUpdate(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetIds: IntArray
    ) {
        // 对每个小组件实例进行更新
        for (appWidgetId in appWidgetIds) {
            updateAppWidget(context, appWidgetManager, appWidgetId)
        }
    }

    private fun updateAppWidget(
        context: Context,
        appWidgetManager: AppWidgetManager,
        appWidgetId: Int
    ) {
        // 加载未取的快递信息
        CoroutineScope(Dispatchers.IO).launch {
            val db = AppDatabase.getDatabase(context)
            val expressInfoList = db.expressDao().getUnpickedExpressInfo().firstOrNull() ?: emptyList()

            // 构建小组件视图
            val views = RemoteViews(context.packageName, R.layout.widget_layout)

            if (expressInfoList.isEmpty()) {
                // 没有未取的快递
                views.setViewVisibility(R.id.tvNoExpress, RemoteViews.VISIBLE)
                views.setViewVisibility(R.id.llExpressList, RemoteViews.GONE)
            } else {
                // 有未取的快递
                views.setViewVisibility(R.id.tvNoExpress, RemoteViews.GONE)
                views.setViewVisibility(R.id.llExpressList, RemoteViews.VISIBLE)

                // 显示最近的3个快递
                val count = minOf(expressInfoList.size, 3)
                for (i in 0 until count) {
                    val expressInfo = expressInfoList[i]
                    val viewId = when (i) {
                        0 -> R.id.llExpress1
                        1 -> R.id.llExpress2
                        2 -> R.id.llExpress3
                        else -> -1
                    }

                    if (viewId != -1) {
                        val expressView = RemoteViews(context.packageName, R.layout.widget_item_express)
                        expressView.setTextViewText(R.id.tvWidgetCompany, expressInfo.company)
                        expressView.setTextViewText(R.id.tvWidgetCode, expressInfo.code)
                        expressView.setTextViewText(R.id.tvWidgetStation, expressInfo.station)
                        views.addView(viewId, expressView)
                    }
                }

                // 隐藏未使用的视图
                for (i in count until 3) {
                    val viewId = when (i) {
                        0 -> R.id.llExpress1
                        1 -> R.id.llExpress2
                        2 -> R.id.llExpress3
                        else -> -1
                    }
                    if (viewId != -1) {
                        views.setViewVisibility(viewId, RemoteViews.GONE)
                    }
                }
            }

            // 设置点击事件，打开应用
            val intent = Intent(context, MainActivity::class.java)
            val pendingIntent = PendingIntent.getActivity(
                context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            views.setOnClickPendingIntent(R.id.widget_root, pendingIntent)

            // 更新小组件
            appWidgetManager.updateAppWidget(appWidgetId, views)
        }
    }

    override fun onEnabled(context: Context) {
        // 小组件首次添加到桌面时调用
    }

    override fun onDisabled(context: Context) {
        // 最后一个小组件从桌面移除时调用
    }
}