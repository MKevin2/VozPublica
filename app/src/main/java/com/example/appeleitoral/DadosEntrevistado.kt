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
import android.text.InputType
import android.text.TextWatcher
import android.text.Editable

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

        etNome = findViewById(R.id.etNome)
        etCelular = findViewById(R.id.etCelular)
        btConfirmar = findViewById(R.id.btConfirmar)

        etNome.inputType = InputType.TYPE_CLASS_TEXT or
                InputType.TYPE_TEXT_FLAG_CAP_WORDS

        etCelular.inputType = InputType.TYPE_CLASS_PHONE

        etCelular.addTextChangedListener(object : TextWatcher {
            private var atualizando = false

            override fun beforeTextChanged(
                s: CharSequence?,
                start: Int,
                count: Int,
                after: Int
            ) {
            }
            override fun onTextChanged(
                s: CharSequence?,
                start: Int,
                before: Int,
                count: Int
            ) {
            }
            override fun afterTextChanged(s: Editable?) {
                if (atualizando) return
                atualizando = true
                val numeros = s.toString().replace(Regex("[^0-9]"), "")
                val formatado = when {
                    numeros.length <= 2 -> {
                        numeros
                    }
                    numeros.length <= 7 -> {
                        "(${numeros.substring(0, 2)}) " +
                                numeros.substring(2)
                    }
                    numeros.length <= 11 -> {
                        "(${numeros.substring(0, 2)}) " +
                                numeros.substring(2, 7) +
                                "-" +
                                numeros.substring(7)
                    }
                    else -> {
                        "(${numeros.substring(0, 2)}) " +
                                numeros.substring(2, 7) +
                                "-" +
                                numeros.substring(7, 11)
                    }
                }
                etCelular.setText(formatado)
                etCelular.setSelection(formatado.length)

                atualizando = false
            }
        })

        // Inicializa o cliente de localização
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        btConfirmar.setOnClickListener {

            val nome = etNome.text.toString().trim()
            val celular = etCelular.text.toString().trim()

            // Verifica se o nome foi preenchido
            if (nome.isEmpty()) {
                etNome.error = "Informe o nome"
                etNome.requestFocus()
                return@setOnClickListener
            }

            // Verifica se o nome possui números
            if (nome.any { it.isDigit() }) {
                etNome.error = "O nome não pode conter números"
                etNome.requestFocus()
                return@setOnClickListener
            }

            // Verifica se o celular foi preenchido
            if (celular.isEmpty()) {
                etCelular.error = "Informe o celular"
                etCelular.requestFocus()
                return@setOnClickListener
            }

            // Retira a máscara do celular
            val numerosCelular = celular.replace(Regex("[^0-9]"), "")

            // Verifica se possui 11 números
            if (numerosCelular.length != 11) {
                etCelular.error = "Informe um celular válido"
                etCelular.requestFocus()
                return@setOnClickListener
            }
            // Se tudo estiver correto, continua para o GPS
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
            "dataHora" to dataFormatada
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