package com.example.appeleitoral

import android.R.attr.delay
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ProgressBar
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.time.delay

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val progressBar = findViewById<ProgressBar>(R.id.pgb)

        lifecycleScope.launch {
            val tempoTotalMs = 5000L
            val passos = 100
            val tempoPorPasso = tempoTotalMs / passos

            for (i in 1..passos) {
                delay(tempoPorPasso)
                progressBar.progress = i
            }

            val intent = Intent(this@MainActivity, Estimulada::class.java)
            startActivity(intent)

            finish()
        }
    }
}