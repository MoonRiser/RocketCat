pluginManagement {
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
rootProject.name = "RocketCat"
include("address-selector")
include("app")
include("common")
include(":ksp")
//include ':address-selector'
//include ':app'
//include ':common'
//rootProject.name='RocketCat'
//setBinding(new Binding([gradle: this]))
//evaluate(new File(
//        settingsDir.parentFile,
//        'flutter_module/.android/include_flutter.groovy'
//))


