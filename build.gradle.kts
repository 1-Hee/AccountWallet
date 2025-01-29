// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false


    id("org.jetbrains.kotlin.kapt") version("2.1.20-Beta1")
}

buildscript {
    repositories {
        google()  // Google Maven repository
        mavenCentral()
    }

    dependencies {
        classpath("com.google.android.gms:oss-licenses-plugin:0.10.6")
    }
}

