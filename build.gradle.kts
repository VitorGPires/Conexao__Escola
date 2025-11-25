// build.gradle.kts (Arquivo da RAIZ do Projeto)

plugins {
    id("com.android.application") version "8.3.2" apply  false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.2" apply false
    // LINHA ADICIONADA: Declara o plugin de serialização e sua versão
    id("org.jetbrains.kotlin.plugin.serialization") version "1.9.0" apply false
    id("com.google.firebase.firebase-perf") version "1.4.2" apply false

}
