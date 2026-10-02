package com.example.appeleitoral

import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.view.Gravity
import android.widget.Button
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.util.Locale

class Resultados : AppCompatActivity() {

    private lateinit var tvTotalEntrevistados: TextView
    private lateinit var graficoPizza: GraficoPizza
    private lateinit var layoutResultados: LinearLayout
    private lateinit var layoutLegenda: LinearLayout
    private lateinit var btVoltar: Button

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_resultados)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Componentes da tela
        tvTotalEntrevistados = findViewById<TextView>(R.id.tvTotalEntrevistados)
        graficoPizza = findViewById<GraficoPizza>(R.id.graficoPizza)
        layoutResultados = findViewById<LinearLayout>(R.id.layoutResultados)
        layoutLegenda = findViewById<LinearLayout>(R.id.layoutLegenda)
        btVoltar = findViewById<Button>(R.id.btVoltar)


        btVoltar.setOnClickListener {
            val intentMenu = Intent(this, MenuResultado::class.java)
            startActivity(intentMenu)
            finish()
        }

        carregarResultados()
    }


    private fun carregarResultados() {

        db.collection("respostas_eleitorais")
            .get()
            .addOnSuccessListener { resultado ->
                val contagem = mutableMapOf<String, Int>()
                // Percorre todas as pesquisas
                for (documento in resultado.documents) {
                    val voto =
                        documento.getString("votoEstimulado")
                    val opcao = when (voto) {
                        null -> "Desejo não responder"
                        else -> voto
                    }
                    contagem[opcao] =
                        (contagem[opcao] ?: 0) + 1
                }

                // Quantidade total de entrevistados
                val total = resultado.size()

                tvTotalEntrevistados.text = "Quant. de pessoas entrevistadas: $total"

                // Mostra os resultados
                mostrarResultados(contagem, total
                )
            }
            .addOnFailureListener { erro ->
                Toast.makeText(
                    this,
                    "Erro ao carregar resultados: ${erro.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }

    private fun mostrarResultados(
        contagem: Map<String, Int>,
        total: Int
    ) {
        // Limpa os resultados anteriores
        layoutResultados.removeAllViews()
        layoutLegenda.removeAllViews()

        // Pega os valores para o gráfico
        val valores = contagem.values.toList()

        // Atualiza o gráfico
        graficoPizza.atualizarDados(valores)


        // Pega as cores utilizadas pelo gráfico
        val cores = graficoPizza.obterCores()

        // Percorre cada resultado
        for ((index, entrada) in contagem.entries.withIndex()) {

            val nome = entrada.key
            val quantidade = entrada.value

            // Calcula porcentagem
            val porcentagem =
                if (total > 0) {
                    quantidade.toDouble() /
                            total * 100
                } else {
                    0.0
                }

            // -----------------------------
            // RESULTADO EM TEXTO
            // -----------------------------
            val texto =
                TextView(this)
            texto.text =
                String.format(
                    Locale("pt", "BR"),
                    "%s - %d - %.1f%%",
                    nome,
                    quantidade,
                    porcentagem
                )
            texto.textSize = 16f
            texto.setTextColor(
                Color.BLACK
            )

            texto.setPadding(
                5,
                4,
                5,
                4
            )

            layoutResultados.addView(
                texto
            )
            // -----------------------------
            // LEGENDA COLORIDA
            // -----------------------------
            criarLegenda(
                nome,
                quantidade,
                porcentagem,
                cores[index % cores.size]
            )
        }
    }

    private fun criarLegenda(
        nome: String,
        quantidade: Int,
        porcentagem: Double,
        cor: Int
    ) {
        // Linha da legenda
        val linha = LinearLayout(this)

        linha.orientation = LinearLayout.HORIZONTAL
        linha.gravity = Gravity.CENTER_VERTICAL

        // Margem da linha
        val margem = 6
        val parametrosLinha = LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT,
                LinearLayout.LayoutParams.WRAP_CONTENT
            )

        parametrosLinha.setMargins(
            0,
            margem,
            0,
            margem
        )

        linha.layoutParams =
            parametrosLinha
        // -----------------------------
        // QUADRADO COLORIDO
        // -----------------------------
        val marcador =
            TextView(this)
        marcador.setBackgroundColor(
            cor
        )
        val parametrosMarcador =
            LinearLayout.LayoutParams(
                25,
                25
            )

        parametrosMarcador.setMargins(
            0,
            0,
            10,
            0
        )

        marcador.layoutParams =
            parametrosMarcador

        // -----------------------------
        // TEXTO DA LEGENDA
        // -----------------------------
        val texto = TextView(this)

        texto.text =
            String.format(
                Locale("pt", "BR"),
                "%s - %d voto(s) - %.1f%%",
                nome,
                quantidade,
                porcentagem
            )

        texto.textSize = 14f
        texto.setTextColor(
            Color.BLACK
        )

        // Adiciona os componentes
        linha.addView(
            marcador
        )
        linha.addView(
            texto
        )

        // Adiciona a linha na legenda
        layoutLegenda.addView(
            linha
        )
    }
}