package com.example.appeleitoral

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class Login : AppCompatActivity() {

private lateinit var etUsuario  : EditText
private lateinit var etSenha  : EditText
private lateinit var btAcessar  : Button
private lateinit var btFinalizar  : Button
private lateinit var tvMensagem  : TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_login)

        etUsuario = findViewById<EditText>(R.id.etUsuario)
        etSenha = findViewById<EditText>(R.id.etSenha)

        btAcessar = findViewById<Button>(R.id.btAcessar)
        btFinalizar = findViewById<Button>(R.id.btFinalizar)

        tvMensagem = findViewById<TextView>(R.id.tvMensagem)

        btAcessar.setOnClickListener {

            val usuario = etUsuario.text.toString().trim()
            val senha = etSenha.text.toString()

            if (usuario == "entrevistador" && senha == "entrevistador") {

                val intent = Intent(this, Espontanea::class.java)
                startActivity(intent)

                finish()

            } else if (usuario == "admin" && senha == "admin") {

                val intent = Intent(this, MenuResultado::class.java)
                startActivity(intent)

                finish()

            } else {

                tvMensagem.text = "Usuário ou Senha Inválidos!"
            }
        }

        btFinalizar.setOnClickListener {

            AlertDialog.Builder(this)
                .setTitle("Finalizar aplicativo")
                .setMessage("Deseja realmente sair do aplicativo?")
                .setNegativeButton("CANCELAR", null)
                .setPositiveButton("SAIR") { _, _ ->

                    finishAffinity()
                }
                .show()
        }
    }
}