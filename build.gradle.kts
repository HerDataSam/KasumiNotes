// Top-level build file where you can add configuration options common to all sub-projects/modules.

buildscript {
    repositories {
        google()
        mavenCentral()
        maven(url = "https://jitpack.io")
    }
    dependencies {
        classpath("com.android.tools.build:gradle:8.7.2")
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
        classpath("com.github.dcendents:android-maven-gradle-plugin:1.5")
    }
}

allprojects {
    repositories {
        google()
        mavenCentral()
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