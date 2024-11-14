plugins {
    id("com.android.application")
    kotlin("android")
    kotlin("kapt")
    id("com.google.devtools.ksp")
    id("androidx.navigation.safeargs")
    id("com.google.android.gms.oss-licenses-plugin")
}

android {
    compileSdk = 35

    defaultConfig {
        applicationId = "com.github.herdatasam.kasuminotes"
        minSdk = 23
        targetSdk = 35
        versionCode = 66
        versionName = "1.2.0k"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isShrinkResources = false
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
        getByName("debug") {
            applicationIdSuffix = ".debug"
            isDebuggable = true
        }
    }

    compileOptions {
        isCoreLibraryDesugaringEnabled = true
        sourceCompatibility = JavaVersion.VERSION_18
        targetCompatibility = JavaVersion.VERSION_18
    }

    kotlinOptions {
        jvmTarget = "18"
    }

    buildFeatures {
        buildConfig = true
        dataBinding = true
        viewBinding = true
    }

    namespace = "com.github.malitsplus.shizurunotes"

    sourceSets {
        getByName("main") {
            res.srcDir("src/main/res/items")
        }
    }
}

dependencies {
    implementation(libs.kspSymbolProcessingApi)
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(libs.appcompat)
    implementation(libs.coreKtx)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigationFragmentKtx)
    implementation(libs.navigationUiKtx)

    implementation(libs.lifecycleExtensions)
    implementation(libs.junit)
    implementation(libs.legacySupportV4)
    implementation(libs.recyclerview)
    implementation(libs.lifecycleViewmodelKtx)
    implementation(libs.lifecycleLivedataKtx)
    androidTestImplementation(libs.testExtJunit)
    androidTestImplementation(libs.espressoCore)

    implementation(files("libs/commons-compress-1.19.jar"))
    implementation(project(":calendarview"))

    ksp(libs.glideCompiler)
    implementation(libs.glide)
    implementation(libs.okhttp)
    implementation(libs.gson)
    implementation(libs.preferenceKtx)
    implementation(libs.guava)
    implementation(libs.viewpager2)

    implementation(libs.processPhoenix)
    implementation(libs.superTextView)
    implementation(libs.materialDialogsCore)
    implementation(libs.materialSearchBar)

    coreLibraryDesugaring(libs.desugarJdkLibs)

    // Some features
    implementation(libs.powerSpinner)

    // Parser
    implementation(libs.jsoup)

    // Room components
    implementation(libs.roomRuntime)
    ksp(libs.roomCompiler)
    implementation(libs.roomKtx)
}
