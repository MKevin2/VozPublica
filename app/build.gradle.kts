plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.appeleitoral"
    compileSdk = 37 // Sintaxe corrigida e versão estabilizada

    defaultConfig {
        applicationId = "com.example.appeleitoral"
        minSdk = 31
        targetSdk = 37 // Ajustado para corresponder ao compileSdk
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false // Sintaxe correta para o Kotlin DSL
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)

    // Importa a plataforma do Firebase (BOM)
    implementation(platform("com.google.firebase:firebase-bom:32.8.0"))

    // Adiciona a biblioteca do Firestore
    implementation("com.google.firebase:firebase-firestore")

    // Salvar Localização
    implementation("com.google.android.gms:play-services-location:21.2.0")
}