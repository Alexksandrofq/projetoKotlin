package com.aplicativo.myapplication

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.aplicativo.myapplication.databinding.ItemAlarmBinding

class AlarmAdapter(
    private val alarms: List<Alarm>,
    private val onEdit: (Alarm) -> Unit,
    private val onDelete: (Alarm) -> Unit
) : RecyclerView.Adapter<AlarmAdapter.AlarmViewHolder>() {

    inner class AlarmViewHolder(val binding: ItemAlarmBinding) :
        RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AlarmViewHolder {
        val binding = ItemAlarmBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return AlarmViewHolder(binding)
    }

    override fun onBindViewHolder(holder: AlarmViewHolder, position: Int) {
        val alarm = alarms[position]
        with(holder.binding) {
            tvTime.text = String.format("%02d:%02d", alarm.hour, alarm.minute)
            btnEdit.setOnClickListener { onEdit(alarm) }
            btnDelete.setOnClickListener { onDelete(alarm) }
        }
    }

    override fun getItemCount(): Int = alarms.size
}
