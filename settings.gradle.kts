pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()  // Agregado para mejor manejo de plugins
        maven { url = uri("https://storage.googleapis.com/mediapipe-release") }
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://storage.googleapis.com/mediapipe-release") }
    }
}

rootProject.name = "cuaderno de comunicaciones"
include(":app")
include(":app:loginscreen")
