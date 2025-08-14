plugins {
    id("com.android.library")
}

android {
    compileSdk = 36

    defaultConfig {
        minSdk = 14
        targetSdk = 36
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                "proguard-rules.pro"
            )
        }
    }

    lint {
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    namespace = "com.haibin.calendarview"
}

tasks.register<Copy>("intoJar") {
    delete("build/libs/CalendarView.jar")
    from("build/intermediates/bundles/release/")
    into("build/libs/")
    include("classes.jar")
    rename("classes.jar", "CalendarView.jar")
}.configure {
    dependsOn(tasks.named("build"))
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    androidTestImplementation(libs.espressoCore) {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    implementation(libs.appcompat)
    implementation(libs.recyclerview)
    testImplementation(libs.junit)
}
