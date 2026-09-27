package com.example.appeleitoral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Espontanea : AppCompatActivity() {

    private lateinit var tvTitulo: TextView
    private lateinit var tvPergunta: TextView

    private lateinit var etResposta: EditText

    private lateinit var cbNaoResponder: CheckBox

    private lateinit var btConfirmar: Button

    private var votoEspontaneo: String? = null
    private var respondeu: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_espontanea)

        tvTitulo = findViewById<TextView>(R.id.tvTitulo)

        tvPergunta = findViewById<TextView>(R.id.tvPergunta)

        etResposta = findViewById<EditText>(R.id.etResposta)

        cbNaoResponder = findViewById<CheckBox>(R.id.cbNaoResponder)

        btConfirmar = findViewById<Button>(R.id.btConfirmar)

        // Opção "Não sei responder"
        cbNaoResponder.setOnClickListener {

            if (cbNaoResponder.isChecked) {

                votoEspontaneo = null
                respondeu = true

                etResposta.setText("")
                etResposta.isEnabled = false

            } else {

                respondeu = false
                etResposta.isEnabled = true
            }
        }

        btConfirmar.setOnClickListener {

            if (cbNaoResponder.isChecked) {

                votoEspontaneo = null
                respondeu = true

            } else {

                val resposta = etResposta.text.toString().trim()

                if (resposta.isEmpty()) {
                    return@setOnClickListener
                }

                votoEspontaneo = resposta
                respondeu = true
            }

            // Vai para a pesquisa estimulada
            val intent = Intent(this, Estimulada::class.java)

            intent.putExtra("votoEspontaneo", votoEspontaneo)

            startActivity(intent)
        }
    }
}