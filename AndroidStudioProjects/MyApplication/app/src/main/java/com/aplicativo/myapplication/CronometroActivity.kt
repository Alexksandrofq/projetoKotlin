package com.aplicativo.myapplication
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer
import androidx.appcompat.app.AppCompatActivity

class CronometroActivity : AppCompatActivity() {
    private lateinit var cronometro: Chronometer
    private var running = false
    private var pauseOffset: Long = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_cronometro)

        cronometro = findViewById(R.id.cronometro)

        findViewById<Button>(R.id.btnStart).setOnClickListener {
            if (!running) {
                cronometro.base = SystemClock.elapsedRealtime() - pauseOffset
                cronometro.start()
                running = true
            }
        }

        findViewById<Button>(R.id.btnPause).setOnClickListener {
            if (running) {
                cronometro.stop()
                pauseOffset = SystemClock.elapsedRealtime() - cronometro.base
                running = false
            }
        }

        findViewById<Button>(R.id.btnReset).setOnClickListener {
            cronometro.base = SystemClock.elapsedRealtime()
            pauseOffset = 0
            if (running) {
                cronometro.stop()
                running = false
            }
        }
    }
}
