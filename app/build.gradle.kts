plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
}

android {
    compileSdk = 34
    android.buildFeatures.dataBinding = true
    android.buildFeatures.viewBinding = true
    compileOptions {
        // Flag to enable support for the new language APIs
        isCoreLibraryDesugaringEnabled = true
        // Sets Java compatibility to Java 8
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    defaultConfig {
        applicationId = "com.example.rocketcat"
        minSdk = 23
        targetSdk = 32
        versionCode = 1
        versionName = "1.0"
        multiDexEnabled = true

    }
    buildTypes {
        debug {
            isMinifyEnabled = false
            isShrinkResources = false
            proguardFiles(getDefaultProguardFile("proguard-android.txt"), "proguard-rules.pro")
        }

        release {
            isMinifyEnabled = true//开启混淆
            isShrinkResources = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }

    }
    namespace = "com.example.rocketcat"
    buildToolsVersion = "34.0.0"

}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":common"))

//    implementation project(":flutter")

    implementation(libs.kotlin.reflect)
    coreLibraryDesugaring(libs.desugar.jdk.libs)

}
