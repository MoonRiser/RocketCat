buildscript {

    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        maven { url = uri("https://jitpack.io") }
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.android) apply false
}

allprojects {
    repositories {
        gradlePluginPortal()
        google()
        mavenCentral()
        maven {
            url = uri("https://maven.aliyun.com/repository/public/")
        }
        maven { url = uri("https://jitpack.io") }
    }
}

tasks.register("clean") {
    delete(rootProject.layout.buildDirectory)
}
