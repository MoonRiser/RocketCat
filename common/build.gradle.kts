plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    id("kotlin-kapt")
    alias(libs.plugins.ksp)
}


android {
    compileSdk = 34
    android.buildFeatures.dataBinding = true


    defaultConfig {
        minSdk = 23
        lint.targetSdk = 31
        consumerProguardFiles("consumer-rules.pro")
    }

    buildTypes {
        debug {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
        release {
            isMinifyEnabled = true
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    namespace = "com.example.common"
    buildToolsVersion = "34.0.0"
}

kotlin {
    sourceSets.main {
        kotlin.srcDir("build/generated/ksp/main/kotlin")
    }
    sourceSets.test {
        kotlin.srcDir("build/generated/ksp/test/kotlin")
    }
}

dependencies {
    implementation(fileTree(mapOf("dir" to "libs", "include" to listOf("*.jar"))))
    implementation(project(":ksp"))
    ksp(project(":ksp"))
    //  Androidx
    api(libs.androix.appcompat)
    api(libs.androidx.ktx)
    api(libs.androidx.constraintlayout)
    api(libs.material)
    api(libs.androidx.prefrence)
    api(libs.dynamic.animation)
    //  Lifecycle
    api(libs.lifecycle)
    api(libs.lifecycle.extensions)
    //    paging3
    api(libs.androix.paging3)
    //  Room
    api(libs.androidx.room)
    kapt(libs.androidx.room.compiler)
    //  DataStore
    api(libs.androidx.dataStore)
    //  调色盘
    api(libs.androidx.palette)
    //  协程Coroutine
    api(libs.kotlinx.coroutine)

    //retrofit
    api(libs.retrofit2)
    api(libs.retrofit.converter.gson)
    api(libs.okhttp)

    //旧版兼容
    api(libs.androidx.legacy)

    //glide
    api(libs.glide)
    api(libs.rxBinding)

    //地址选择器
    api(libs.address.selector)

    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso)

}


