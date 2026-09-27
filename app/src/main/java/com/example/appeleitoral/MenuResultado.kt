package com.example.appeleitoral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class MenuResultado : AppCompatActivity() {

    private lateinit var btFinalizar : Button
    private lateinit var btEleitores: Button
    private lateinit var btResultado : Button
    private lateinit var btLimparDados : Button

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_resultado)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btFinalizar = findViewById<Button>(R.id.btFinalizar)
        btEleitores = findViewById<Button>(R.id.btEleitores)
        btResultado = findViewById<Button>(R.id.btResultado)
        btLimparDados = findViewById<Button>(R.id.btLimparDados)

        btEleitores.setOnClickListener {
            val intentMenu = Intent(this, Eleitores::class.java)
            startActivity(intentMenu)
            finish()
        }

        btResultado.setOnClickListener {
            val intentMenu = Intent(this, Resultados::class.java)
            startActivity(intentMenu)
            finish()
        }

        btFinalizar.setOnClickListener {
            finishAffinity()
        }
    }
}