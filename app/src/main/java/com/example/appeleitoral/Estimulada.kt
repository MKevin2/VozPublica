package com.example.appeleitoral

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.cardview.widget.CardView

class Estimulada : AppCompatActivity() {

    private lateinit var cardCandidato1: CardView
    private lateinit var cardCandidato2: CardView
    private lateinit var cardCandidato3: CardView
    private lateinit var cardCandidato4: CardView
    private lateinit var cardCandidato5: CardView

    private lateinit var cardBranco: CardView
    private lateinit var cardNulo: CardView
    private lateinit var cardNaoSei: CardView
    private lateinit var cardNaoResponder: CardView

    private lateinit var btConfirmarEstimulada: Button

    private var respondeu: Boolean = false

    private var candidatoSelecionado: String? = null

    private var cardAnterior: CardView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_estimulada)

        cardCandidato1 = findViewById<CardView>(R.id.cardCandidato1)
        cardCandidato2 = findViewById<CardView>(R.id.cardCandidato2)
        cardCandidato3 = findViewById<CardView>(R.id.cardCandidato3)
        cardCandidato4 = findViewById<CardView>(R.id.cardCandidato4)
        cardCandidato5 = findViewById<CardView>(R.id.cardCandidato5)

        cardBranco = findViewById<CardView>(R.id.cardBranco)
        cardNulo = findViewById<CardView>(R.id.cardNulo)
        cardNaoSei = findViewById<CardView>(R.id.cardNaoSei)
        cardNaoResponder = findViewById<CardView>(R.id.cardNaoResponder)

        btConfirmarEstimulada = findViewById<Button>(R.id.btConfirmarEstimulada)

        cardCandidato1.setOnClickListener {
            candidatoSelecionado = "Carlos Alberto Siqueira"
            respondeu = true
            selecionarCard(cardCandidato1)
        }

        cardCandidato2.setOnClickListener {
            candidatoSelecionado = "Beatriz Helena Gouveia"
            respondeu = true
            selecionarCard(cardCandidato2)
        }

        cardCandidato3.setOnClickListener {
            candidatoSelecionado = "João Xavier de Almeida"
            respondeu = true
            selecionarCard(cardCandidato3)
        }

        cardCandidato4.setOnClickListener {
            candidatoSelecionado = "Mariana Costa Nunes"
            respondeu = true
            selecionarCard(cardCandidato4)
        }

        cardCandidato5.setOnClickListener {
            candidatoSelecionado = "Roberto Sampaio Moura"
            respondeu = true
            selecionarCard(cardCandidato5)
        }

        cardBranco.setOnClickListener {
            candidatoSelecionado = "Branco"
            respondeu = true
            selecionarCard(cardBranco)
        }

        cardNulo.setOnClickListener {
            candidatoSelecionado = "Nulo"
            respondeu = true
            selecionarCard(cardNulo)
        }

        cardNaoSei.setOnClickListener {
            candidatoSelecionado = "Não sei"
            respondeu = true
            selecionarCard(cardNaoSei)
        }

        cardNaoResponder.setOnClickListener {
            candidatoSelecionado = null
            respondeu = true
            selecionarCard(cardNaoResponder)
        }

        btConfirmarEstimulada.setOnClickListener {

            if (!respondeu) {

                Toast.makeText(this,
                    "Por favor, responda à pesquisa.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            val votoEspontaneo = intent.getStringExtra("votoEspontaneo")

            val intent = Intent(this, Problemas::class.java)
            intent.putExtra("votoEspontaneo", votoEspontaneo)
            intent.putExtra("votoEstimulado", candidatoSelecionado)
            startActivity(intent)

        }
    }

    private fun selecionarCard(cardSelecionado: CardView) {

        // Volta o card anterior para a cor original
        cardAnterior?.setCardBackgroundColor(
            Color.WHITE
        )

        // Coloca azul somente no card selecionado
        cardSelecionado.setCardBackgroundColor(
            Color.parseColor("#0183C6")
        )

        // Guarda o card selecionado
        cardAnterior = cardSelecionado
    }
}


