package com.example.appeleitoral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity

class Problemas : AppCompatActivity() {

    private lateinit var cbSaude: CheckBox
    private lateinit var cbEducacao: CheckBox
    private lateinit var cbTransporte: CheckBox
    private lateinit var cbSeguranca: CheckBox
    private lateinit var cbEmprego: CheckBox
    private lateinit var cbMoradia: CheckBox
    private lateinit var cbEconomia: CheckBox
    private lateinit var cbMeioAmbiente: CheckBox
    private lateinit var cbCorrupcao: CheckBox
    private lateinit var cbOutro: CheckBox

    private lateinit var cbNaoResponder: CheckBox

    private lateinit var btConfirmar: Button

    private val problemasSelecionados = mutableListOf<String>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_problemas)

        cbSaude = findViewById<CheckBox>(R.id.cbSaude)
        cbEducacao = findViewById<CheckBox>(R.id.cbEducacao)
        cbTransporte = findViewById<CheckBox>(R.id.cbTransporte)
        cbSeguranca = findViewById<CheckBox>(R.id.cbSeguranca)
        cbEmprego = findViewById<CheckBox>(R.id.cbEmprego)
        cbMoradia = findViewById<CheckBox>(R.id.cbMoradia)
        cbEconomia = findViewById<CheckBox>(R.id.cbEconomia)
        cbMeioAmbiente = findViewById<CheckBox>(R.id.cbMeioAmbiente)
        cbCorrupcao = findViewById<CheckBox>(R.id.cbCorrupcao)
        cbOutro = findViewById<CheckBox>(R.id.cbOutro)

        cbNaoResponder = findViewById<CheckBox>(R.id.cbNaoResponder)

        btConfirmar = findViewById<Button>(R.id.btConfirmar)

        controlarSelecao(cbSaude, "Saúde")
        controlarSelecao(cbEducacao, "Educação")
        controlarSelecao(cbTransporte, "Transporte")
        controlarSelecao(cbSeguranca, "Segurança")
        controlarSelecao(cbEmprego, "Emprego")
        controlarSelecao(cbMoradia, "Moradia")
        controlarSelecao(cbEconomia, "Economia")
        controlarSelecao(cbMeioAmbiente, "Meio Ambiente")
        controlarSelecao(cbCorrupcao, "Corrupção")
        controlarSelecao(cbOutro, "Outro")

        // Opção "Não sei responder"
        cbNaoResponder.setOnCheckedChangeListener { _, marcado ->

            if (marcado) {

                // Desmarca todos os problemas
                cbSaude.isChecked = false
                cbEducacao.isChecked = false
                cbTransporte.isChecked = false
                cbSeguranca.isChecked = false
                cbEmprego.isChecked = false
                cbMoradia.isChecked = false
                cbEconomia.isChecked = false
                cbMeioAmbiente.isChecked = false
                cbCorrupcao.isChecked = false
                cbOutro.isChecked = false

                // Limpa a lista
                problemasSelecionados.clear()
            }
        }

        btConfirmar.setOnClickListener {

            // Se marcou "Não sei responder", pode avançar
            if (cbNaoResponder.isChecked) {

                val problemas = ArrayList<String>()

                val intent = Intent(this, DadosEntrevistado::class.java)

                intent.putStringArrayListExtra(
                    "problemas",
                    problemas
                )

                startActivity(intent)

                return@setOnClickListener
            }

            // Se não marcou "Não sei responder",
            // precisa selecionar EXATAMENTE 3 problemas
            if (problemasSelecionados.size != 3) {

                Toast.makeText(
                    this,
                    "Selecione exatamente 3 problemas.",
                    Toast.LENGTH_SHORT
                ).show()

                return@setOnClickListener
            }

            // Cria a lista com os 3 problemas
            val problemas = ArrayList<String>()

            problemas.addAll(problemasSelecionados)

            // Vai para a próxima tela
            val intent = Intent(this, DadosEntrevistado::class.java)

            intent.putStringArrayListExtra(
                "problemas",
                problemas
            )

            startActivity(intent)
        }
    }

    // Função responsável por controlar cada CheckBox
    private fun controlarSelecao(
        checkBox: CheckBox,
        nomeProblema: String
    ) {

        checkBox.setOnCheckedChangeListener { _, marcado ->

            if (marcado) {

                // Verifica se já existem 3 problemas selecionados
                if (problemasSelecionados.size >= 3) {

                    // Desmarca o quarto problema
                    checkBox.isChecked = false

                    Toast.makeText(
                        this,
                        "Você pode selecionar no máximo 3 problemas.",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {

                    // Adiciona o problema na lista
                    problemasSelecionados.add(nomeProblema)
                }

            } else {

                // Remove o problema quando for desmarcado
                problemasSelecionados.remove(nomeProblema)
            }
        }
    }
}