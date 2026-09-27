package com.example.appeleitoral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class Eleitores : AppCompatActivity() {

    private lateinit var btVoltarMenu : Button
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eleitores)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btVoltarMenu = findViewById<Button>(R.id.btVoltarMenu)

        btVoltarMenu.setOnClickListener {
            val intentMenu = Intent(this, MenuResultado::class.java)
            startActivity(intentMenu)
            finish()
        }
    }
}