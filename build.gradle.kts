plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
}


// **Eliminar este bloque** o comentarlo
// allprojects {
//     repositories {
//         google()
//         mavenCentral()
//         maven { url = uri("https://storage.googleapis.com/mediapipe-release") }
//     }
// }

buildscript {
    dependencies {
        classpath("org.jetbrains.kotlin:kotlin-gradle-plugin:1.9.10")
    }
}

