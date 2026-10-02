package com.example.appeleitoral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore

class MenuResultado : AppCompatActivity() {

    private lateinit var btDeslogar: Button
    private lateinit var btEleitores: Button
    private lateinit var btResultado: Button
    private lateinit var btLimparDados: Button

    private val db = Firebase.firestore

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_menu_resultado)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())

            v.setPadding(
                systemBars.left,
                systemBars.top,
                systemBars.right,
                systemBars.bottom
            )

            insets
        }

        btDeslogar = findViewById<Button>(R.id.btDeslogar)
        btEleitores = findViewById<Button>(R.id.btEleitores)
        btResultado = findViewById<Button>(R.id.btResultado)
        btLimparDados = findViewById<Button>(R.id.btLimparDados)

        // Abrir tela de eleitores
        btEleitores.setOnClickListener {
            val intentMenu = Intent(this, Eleitores::class.java)
            startActivity(intentMenu)
            finish()
        }

        // Abrir tela de resultados
        btResultado.setOnClickListener {
            val intentMenu = Intent(this, Resultados::class.java)
            startActivity(intentMenu)
            finish()
        }

        // Limpar dados do banco
        btLimparDados.setOnClickListener {
            confirmarLimpezaDados()
        }

        // Deslogar
        btDeslogar.setOnClickListener {
            AlertDialog.Builder(this)
                .setTitle("Deslogar")
                .setMessage("Deseja realmente sair da conta?")
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("DESLOGAR") { _, _ ->

                    val intentLogin = Intent(this, Login::class.java)
                    intentLogin.flags =
                        Intent.FLAG_ACTIVITY_NEW_TASK or
                                Intent.FLAG_ACTIVITY_CLEAR_TASK

                    startActivity(intentLogin)
                }
                .show()
        }
    }

    private fun confirmarLimpezaDados() {

        AlertDialog.Builder(this)
            .setTitle("Limpar dados")
            .setMessage(
                "Deseja realmente apagar todas as pesquisas do banco de dados?\n\n" +
                        "Essa ação não poderá ser desfeita."
            )
            .setNegativeButton("CANCELAR", null)
            .setPositiveButton("APAGAR") { _, _ ->

                limparDadosFirebase()
            }
            .show()
    }

    private fun limparDadosFirebase() {

        db.collection("respostas_eleitorais")
            .get()
            .addOnSuccessListener { resultado ->

                if (resultado.isEmpty) {
                    Toast.makeText(
                        this,
                        "Não existem dados para apagar.",
                        Toast.LENGTH_SHORT
                    ).show()

                    return@addOnSuccessListener
                }

                val batch = db.batch()

                for (documento in resultado.documents) {
                    batch.delete(documento.reference)
                }

                batch.commit()
                    .addOnSuccessListener {
                        Toast.makeText(
                            this,
                            "Todos os dados foram apagados.",
                            Toast.LENGTH_LONG
                        ).show()
                    }
                    .addOnFailureListener { erro ->
                        Toast.makeText(
                            this,
                            "Erro ao apagar os dados: ${erro.message}",
                            Toast.LENGTH_LONG
                        ).show()
                    }
            }
            .addOnFailureListener { erro ->
                Toast.makeText(
                    this,
                    "Erro ao acessar o banco: ${erro.message}",
                    Toast.LENGTH_LONG
                ).show()
            }
    }
}