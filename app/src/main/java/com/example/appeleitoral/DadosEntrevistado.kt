package com.example.appeleitoral

import android.Manifest
import android.annotation.SuppressLint
import android.content.Intent
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationServices
import com.google.firebase.Firebase
import com.google.firebase.firestore.firestore
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class DadosEntrevistado : AppCompatActivity() {

    private lateinit var etNome: EditText
    private lateinit var etCelular: EditText
    private lateinit var btConfirmar: Button

    // Ferramenta do Google para pegar a localização
    private lateinit var fusedLocationClient: FusedLocationProviderClient
    private val db = Firebase.firestore

    // Gestor de permissão para o GPS
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val permissaoConcedida = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true ||
                permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true

        if (permissaoConcedida) {
            obterLocalizacaoESalvar()
        } else {
            Toast.makeText(this, "Permissão de GPS negada. A gravar sem localização.", Toast.LENGTH_SHORT).show()
            salvarPesquisaNoFirebase(null, null)
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_dados_entrevistado)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        // Vincule com os IDs que colocou no seu XML (ajuste se tiverem nomes diferentes)
        etNome = findViewById(R.id.etNome)
        etCelular = findViewById(R.id.etCelular)
        btConfirmar = findViewById(R.id.btConfirmar)

        // Inicializa o cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btConfirmar.setOnClickListener {
            verificarPermissaoGPS()
        }
    }

    private fun verificarPermissaoGPS() {
        // Verifica se a app já tem permissão
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            obterLocalizacaoESalvar()
        } else {
            // Pede a permissão ao utilizador
            requestPermissionLauncher.launch(
                arrayOf(Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION)
            )
        }
    }

    @SuppressLint("MissingPermission")
    private fun obterLocalizacaoESalvar() {
        fusedLocationClient.lastLocation
            .addOnSuccessListener { location: Location? ->
                if (location != null) {
                    salvarPesquisaNoFirebase(location.latitude, location.longitude)
                } else {
                    // Caso o GPS do telemóvel esteja desligado ou sem sinal
                    Toast.makeText(this, "Não foi possível obter a localização exata.", Toast.LENGTH_SHORT).show()
                    salvarPesquisaNoFirebase(null, null)
                }
            }
            .addOnFailureListener {
                salvarPesquisaNoFirebase(null, null)
            }
    }

    private fun salvarPesquisaNoFirebase(latitude: Double?, longitude: Double?) {
        // 1. Desempacota a "mochila" de intents (Ecrãs anteriores)
        val votoEspontaneo = intent.getStringExtra("votoEspontaneo")
        val votoEstimulado = intent.getStringExtra("votoEstimulado")
        val problemas = intent.getStringArrayListExtra("problemas")?.toList() ?: emptyList()

        // 2. Extrai os textos preenchidos nesta última tela
        val nome = etNome.text.toString().trim()
        val celular = etCelular.text.toString().trim()

        val dataFormatada = SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault()).format(Date())

        // 3. Monta o documento completo
        val pesquisaCompleta = hashMapOf(
            "votoEspontaneo" to votoEspontaneo,
            "votoEstimulado" to votoEstimulado,
            "problemasCitados" to problemas,
            "entrevistado" to hashMapOf(
                "nome" to nome,
                "celular" to celular
            ),
            "localizacao" to hashMapOf(
                "latitude" to latitude,
                "longitude" to longitude
            ),
            "dataHora" to dataFormatada // Grava a data/hora automaticamente em milissegundos
        )

        // 4. Salva no banco de dados e reinicia
        db.collection("respostas_eleitorais")
            .add(pesquisaCompleta)
            .addOnSuccessListener {
                Toast.makeText(this, "Pesquisa finalizada e gravada!", Toast.LENGTH_LONG).show()

                val intentMenu = Intent(this, Espontanea::class.java)
                intentMenu.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                startActivity(intentMenu)
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Erro ao gravar: ${e.message}", Toast.LENGTH_LONG).show()
            }
    }
}