package com.example.appeleitoral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class Eleitores : AppCompatActivity() {

    private lateinit var btVoltarMenu: Button

    // Nova variável para o texto da lista
    private lateinit var tvListaRegistos: TextView

    // Inicializa a ligação ao banco de dados
    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_eleitores)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        btVoltarMenu = findViewById(R.id.btVoltarMenu)
        tvListaRegistos = findViewById(R.id.tvListaRegistos)

        // Botão Voltar para o menu
        btVoltarMenu.setOnClickListener {
            val intentMenu = Intent(this, MenuResultado::class.java)
            startActivity(intentMenu)
            finish()
        }

        // Inicia o carregamento dos dados automaticamente
        carregarDadosDeFormaSimples()
    }

    private fun carregarDadosDeFormaSimples() {
        tvListaRegistos.text = "Carregando os dados..."

        db.collection("respostas_eleitorais")
            .orderBy("timestamp", com.google.firebase.firestore.Query.Direction.ASCENDING) // Ordenando os registros
            .get()
            .addOnSuccessListener { result ->
                val construtorDeTexto = java.lang.StringBuilder()

                for (document in result) {
                    val entrevistado = document.get("entrevistado") as? Map<String, Any>
                    val nome = entrevistado?.get("nome") as? String ?: "Não informado"
                    val celular = entrevistado?.get("celular") as? String ?: "Não informado"

                    val dataHora = document.getString("dataHora") ?: "Não registada"

                    val localizacaoMap = document.get("localizacao") as? Map<String, Any>
                    val lat = localizacaoMap?.get("latitude")
                    val lon = localizacaoMap?.get("longitude")
                    val localStr = if (lat != null && lon != null) "$lat, $lon" else "Sem GPS"

                    construtorDeTexto.append("Nome: $nome\n")
                    construtorDeTexto.append("Celular: $celular\n")
                    construtorDeTexto.append("Data/Hora: $dataHora\n")
                    construtorDeTexto.append("Local: $localStr\n")
                    construtorDeTexto.append("-----------------------------------\n\n")
                }

                if (construtorDeTexto.isEmpty()) {
                    tvListaRegistos.text = "Nenhuma pesquisa encontrada."
                } else {
                    tvListaRegistos.text = construtorDeTexto.toString()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "Erro ao carregar do banco de dados", Toast.LENGTH_SHORT).show()
                tvListaRegistos.text = "Falha ao carregar."
            }
    }
}