plugins {
    id("com.android.library")
}

android {
    compileSdk = 35

    defaultConfig {
        minSdk = 14
        targetSdk = 35
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
    androidTestImplementation("androidx.test.espresso:espresso-core:3.1.0") {
        exclude(group = "com.android.support", module = "support-annotations")
    }
    implementation("androidx.appcompat:appcompat:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.0.0")
    testImplementation("junit:junit:4.13.2")
}
