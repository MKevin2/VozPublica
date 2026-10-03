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
    private lateinit var btVoltar: Button

    // Elementos da Espontânea
    private lateinit var layoutEspontanea: LinearLayout

    // Elementos da Estimulada
    private lateinit var graficoEstimulada: GraficoPizza
    private lateinit var layoutLegendaEstimulada: LinearLayout

    // Elementos de Problemas
    private lateinit var graficoProblemas: GraficoPizza
    private lateinit var layoutLegendaProblemas: LinearLayout

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

        tvTotalEntrevistados = findViewById(R.id.tvTotalEntrevistados)
        btVoltar = findViewById(R.id.btVoltar)

        layoutEspontanea = findViewById(R.id.layoutEspontanea)

        graficoEstimulada = findViewById(R.id.graficoEstimulada)
        layoutLegendaEstimulada = findViewById(R.id.layoutLegendaEstimulada)

        graficoProblemas = findViewById(R.id.graficoProblemas)
        layoutLegendaProblemas = findViewById(R.id.layoutLegendaProblemas)

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
                val totalEntrevistados = resultado.size()
                tvTotalEntrevistados.text = "Total de Entrevistas: $totalEntrevistados"

                val contagemEspontanea = mutableMapOf<String, Int>()
                val contagemEstimulada = mutableMapOf<String, Int>()
                val contagemProblemas = mutableMapOf<String, Int>()

                for (documento in resultado.documents) {
                    // 1. Processar Espontânea (Padronizar para maiúsculas e sem espaços extras)
                    val votoEsp = documento.getString("votoEspontaneo")
                    val opcaoEsp = if (votoEsp.isNullOrBlank()) "NÃO SABE / NÃO RESPONDEU" else votoEsp.trim().uppercase(Locale.getDefault())
                    contagemEspontanea[opcaoEsp] = contagemEspontanea.getOrDefault(opcaoEsp, 0) + 1

                    // 2. Processar Estimulada
                    val votoEst = documento.getString("votoEstimulado")
                    val opcaoEst = votoEst ?: "Não respondeu"
                    contagemEstimulada[opcaoEst] = contagemEstimulada.getOrDefault(opcaoEst, 0) + 1

                    // 3. Processar Problemas (É uma lista)
                    val problemas = documento.get("problemasCitados") as? List<String> ?: emptyList()
                    if (problemas.isEmpty()) {
                        contagemProblemas["Nenhum"] = contagemProblemas.getOrDefault("Nenhum", 0) + 1
                    } else {
                        for (problema in problemas) {
                            contagemProblemas[problema] = contagemProblemas.getOrDefault(problema, 0) + 1
                        }
                    }
                }

                // Renderiza as telas
                mostrarSecaoTexto(contagemEspontanea, totalEntrevistados, layoutEspontanea)
                mostrarSecaoGrafico(contagemEstimulada, totalEntrevistados, graficoEstimulada, layoutLegendaEstimulada)
                mostrarSecaoGrafico(contagemProblemas, totalEntrevistados, graficoProblemas, layoutLegendaProblemas)
            }
            .addOnFailureListener { erro ->
                Toast.makeText(this, "Erro: ${erro.message}", Toast.LENGTH_LONG).show()
            }
    }

    // Função para renderizar listas simples de texto (Usado na Espontânea)
    private fun mostrarSecaoTexto(contagem: Map<String, Int>, total: Int, layout: LinearLayout) {
        layout.removeAllViews()

        // Ordena do mais votado para o menos votado
        val ordenado = contagem.entries.sortedByDescending { it.value }

        for (entrada in ordenado) {
            val porcentagem = if (total > 0) (entrada.value.toDouble() / total) * 100 else 0.0

            val texto = TextView(this)
            texto.text = String.format(Locale("pt", "BR"), "%s: %d voto(s) (%.1f%%)", entrada.key, entrada.value, porcentagem)
            texto.textSize = 16f
            texto.setTextColor(Color.BLACK)
            texto.setPadding(0, 8, 0, 8)
            layout.addView(texto)
        }
    }

    // Função reaproveitável para desenhar o Gráfico e a Legenda (Usada na Estimulada e Problemas)
    private fun mostrarSecaoGrafico(contagem: Map<String, Int>, total: Int, grafico: GraficoPizza, layoutLegenda: LinearLayout) {
        layoutLegenda.removeAllViews()

        // Ordena do mais votado para o menos votado para o gráfico fazer sentido
        val ordenado = contagem.entries.sortedByDescending { it.value }

        val valores = ordenado.map { it.value }
        grafico.atualizarDados(valores)

        val cores = grafico.obterCores()

        for ((index, entrada) in ordenado.withIndex()) {
            val nome = entrada.key
            val quantidade = entrada.value
            val porcentagem = if (total > 0) (quantidade.toDouble() / total) * 100 else 0.0

            // Linha da legenda
            val linha = LinearLayout(this)
            linha.orientation = LinearLayout.HORIZONTAL
            linha.gravity = Gravity.CENTER_VERTICAL
            val paramsLinha = LinearLayout.LayoutParams(LinearLayout.LayoutParams.MATCH_PARENT, LinearLayout.LayoutParams.WRAP_CONTENT)
            paramsLinha.setMargins(0, 8, 0, 8)
            linha.layoutParams = paramsLinha

            // Quadrado de cor
            val marcador = TextView(this)
            marcador.setBackgroundColor(cores[index % cores.size])
            val paramsMarcador = LinearLayout.LayoutParams(30, 30)
            paramsMarcador.setMargins(0, 0, 15, 0)
            marcador.layoutParams = paramsMarcador

            // Texto da legenda
            val texto = TextView(this)
            texto.text = String.format(Locale("pt", "BR"), "%s: %d (%.1f%%)", nome, quantidade, porcentagem)
            texto.textSize = 15f
            texto.setTextColor(Color.BLACK)

            linha.addView(marcador)
            linha.addView(texto)
            layoutLegenda.addView(linha)
        }
    }
}