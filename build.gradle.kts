// Top-level build file where you can add configuration options common to all sub-projects/modules.

plugins {
    alias(libs.plugins.android) apply false

    alias(libs.plugins.kotlinAndroid) apply false
    alias(libs.plugins.kotlinKapt) apply false

    alias(libs.plugins.ksp) apply false

    alias(libs.plugins.safeArgs) apply false
    alias(libs.plugins.ossLicenses) apply false
}

allprojects {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }

    // Apply common compiler arguments to all JavaCompile tasks
    tasks.withType<JavaCompile>().configureEach {
        options.compilerArgs.add("-Xlint:deprecation")
        // Uncomment the following line if you need to suppress unchecked warnings
        // options.compilerArgs.add("-Xlint:unchecked")
    }
}

tasks.register("clean", Delete::class) {
    delete(rootProject.layout.buildDirectory)
}