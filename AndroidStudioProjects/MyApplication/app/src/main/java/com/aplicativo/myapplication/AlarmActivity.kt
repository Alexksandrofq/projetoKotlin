package com.aplicativo.myapplication

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.provider.Settings
import android.widget.Button
import android.widget.TimePicker
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import java.util.Calendar

class AlarmActivity : AppCompatActivity() {

    private lateinit var timePicker: TimePicker
    private lateinit var alarmManager: AlarmManager
    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: AlarmAdapter
    private lateinit var dbHelper: AlarmDatabaseHelper

    private var alarmList = mutableListOf<Alarm>()
    private var editingAlarm: Alarm? = null  // controla se está editando algum alarme

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_alarm)

        dbHelper = AlarmDatabaseHelper(this)

        timePicker = findViewById(R.id.timePicker)
        timePicker.setIs24HourView(true)

        recyclerView = findViewById(R.id.recyclerViewAlarms)
        alarmList = dbHelper.getAll().toMutableList()

        val btnSetAlarme = findViewById<Button>(R.id.btnSetAlarme)

        // Garante que botão começa com texto padrão
        btnSetAlarme.text = "Definir Alarme"

        adapter = AlarmAdapter(alarmList,
            onEdit = { alarm ->
                timePicker.hour = alarm.hour
                timePicker.minute = alarm.minute
                editingAlarm = alarm
                btnSetAlarme.text = "Salvar Alarme"  // muda texto para indicar edição
                Toast.makeText(this, "Editando alarme: %02d:%02d".format(alarm.hour, alarm.minute), Toast.LENGTH_SHORT).show()
            },

            onDelete = { alarm ->
                dbHelper.delete(alarm.id)
                val index = alarmList.indexOfFirst { it.id == alarm.id }
                if (index != -1) {
                    alarmList.removeAt(index)
                    adapter.notifyItemRemoved(index)
                }
                Toast.makeText(this, "Alarme excluído", Toast.LENGTH_SHORT).show()
            }
        )

        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this)

        btnSetAlarme.setOnClickListener {
            val hour = timePicker.hour
            val minute = timePicker.minute

            if (editingAlarm != null) {
                // Atualiza o alarme existente
                editingAlarm!!.hour = hour
                editingAlarm!!.minute = minute
                dbHelper.update(editingAlarm!!)  // Atualiza no banco

                // Atualiza na lista e notifica o adapter
                val index = alarmList.indexOfFirst { it.id == editingAlarm!!.id }
                if (index != -1) {
                    alarmList[index] = editingAlarm!!
                    adapter.notifyItemChanged(index)
                }
                Toast.makeText(this, "Alarme atualizado para %02d:%02d".format(hour, minute), Toast.LENGTH_SHORT).show()

                // Resetar estado após salvar
                editingAlarm = null
                btnSetAlarme.text = "Definir Alarme"
            } else {
                // Cria novo alarme
                val newAlarm = Alarm(hour = hour, minute = minute)
                newAlarm.id = dbHelper.insert(newAlarm).toInt()
                alarmList.add(newAlarm)
                adapter.notifyItemInserted(alarmList.size - 1)
                Toast.makeText(this, "Alarme definido para %02d:%02d".format(hour, minute), Toast.LENGTH_SHORT).show()
            }

            // Agenda o alarme (use o ID atualizado ou novo)
            scheduleAlarm(hour, minute, editingAlarm?.id ?: alarmList.last().id)
        }
    }

    private fun scheduleAlarm(hour: Int, minute: Int, alarmId: Int) {
        alarmManager = getSystemService(ALARM_SERVICE) as AlarmManager

        val intent = Intent(this, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            this,
            alarmId,
            intent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, hour)
            set(Calendar.MINUTE, minute)
            set(Calendar.SECOND, 0)
            if (before(Calendar.getInstance())) {
                add(Calendar.DATE, 1) // agenda para o próximo dia se hora já passou
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
            if (alarmManager.canScheduleExactAlarms()) {
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
            } else {
                startActivity(Intent(Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM))
                Toast.makeText(this, "Permita agendamento de alarmes exatos nas configurações", Toast.LENGTH_LONG).show()
            }
        } else {
            alarmManager.setExact(AlarmManager.RTC_WAKEUP, calendar.timeInMillis, pendingIntent)
        }
    }
}

