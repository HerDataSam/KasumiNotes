rootProject.name = "KasumiNotes"
include(":app", ":calendarview")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
    plugins {
        id("com.android.application") version "8.5" apply false
        id("org.jetbrains.kotlin.android") version "2.0.20" apply false
        id("org.jetbrains.kotlin.kapt") version "2.0.20" apply false
        id("androidx.navigation.safeargs") version "2.8.4" apply false
        id("com.google.android.gms.oss-licenses-plugin") version "0.10.6" apply false
        id("com.google.devtools.ksp") version "2.0.20-1.0.24" apply false
    }
}

dependencyResolutionManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}
