package com.aplicativo.myapplication

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class MainActivity : AppCompatActivity() {
    private lateinit var tvHora: TextView
    private val handler = Handler(Looper.getMainLooper())
    private val updateClock = object : Runnable {
        override fun run() {
            val sdf = SimpleDateFormat("HH:mm:ss", Locale("pt", "BR"))
            sdf.timeZone = java.util.TimeZone.getTimeZone("America/Sao_Paulo")

            tvHora.text = sdf.format(Date())
            handler.postDelayed(this, 1000)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        tvHora = findViewById(R.id.tvHora)
        findViewById<Button>(R.id.btnAlarme).setOnClickListener {
            startActivity(Intent(this, AlarmActivity::class.java))
        }
        findViewById<Button>(R.id.btnCronometro).setOnClickListener {
            startActivity(Intent(this, CronometroActivity::class.java))
        }
        handler.post(updateClock)
    }

    override fun onDestroy() {
        handler.removeCallbacks(updateClock)
        super.onDestroy()
    }
}
