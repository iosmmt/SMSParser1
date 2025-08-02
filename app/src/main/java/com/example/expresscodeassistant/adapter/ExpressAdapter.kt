package com.example.expresscodeassistant.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.expresscodeassistant.R
import com.example.expresscodeassistant.model.ExpressInfo
import java.text.SimpleDateFormat
import java.util.Locale

class ExpressAdapter(private val onItemClick: (ExpressInfo) -> Unit) :
    ListAdapter<ExpressInfo, ExpressAdapter.ExpressViewHolder>(DiffCallback()) {

    class DiffCallback : DiffUtil.ItemCallback<ExpressInfo>() {
        override fun areItemsTheSame(oldItem: ExpressInfo, newItem: ExpressInfo): Boolean {
            return oldItem.id == newItem.id
        }

        override fun areContentsTheSame(oldItem: ExpressInfo, newItem: ExpressInfo): Boolean {
            return oldItem == newItem
        }
    }

    inner class ExpressViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val tvCompany: TextView = itemView.findViewById(R.id.tvCompany)
        private val tvTime: TextView = itemView.findViewById(R.id.tvTime)
        private val tvCode: TextView = itemView.findViewById(R.id.tvCode)
        private val tvStation: TextView = itemView.findViewById(R.id.tvStation)
        private val tvAddress: TextView = itemView.findViewById(R.id.tvAddress)
        private val tvPhone: TextView = itemView.findViewById(R.id.tvPhone)
        private val btnMarkPicked: Button = itemView.findViewById(R.id.btnMarkPicked)

        fun bind(expressInfo: ExpressInfo) {
            tvCompany.text = expressInfo.company
            tvTime.text = formatDate(expressInfo.timestamp)
            tvCode.text = expressInfo.code
            tvStation.text = expressInfo.station
            tvAddress.text = expressInfo.address
            tvPhone.text = expressInfo.phone ?: ""

            // 设置点击事件
            itemView.setOnClickListener {
                onItemClick(expressInfo)
            }

            // 标记已取按钮
            btnMarkPicked.setOnClickListener {
                onItemClick(expressInfo)
            }

            // 如果已取，则添加删除线
            if (expressInfo.isPicked) {
                tvCode.paint.isStrikeThruText = true
                btnMarkPicked.text = "已取"
                btnMarkPicked.isEnabled = false
            } else {
                tvCode.paint.isStrikeThruText = false
                btnMarkPicked.text = "标记已取"
                btnMarkPicked.isEnabled = true
            }
        }

        private fun formatDate(date: java.util.Date): String {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            return sdf.format(date)
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ExpressViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_express_card, parent, false)
        return ExpressViewHolder(view)
    }

    override fun onBindViewHolder(holder: ExpressViewHolder, position: Int) {
        holder.bind(getItem(position))
    }
}