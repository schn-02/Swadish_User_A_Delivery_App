import java.util.regex.Pattern.compile

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("kotlin-parcelize")
    alias(libs.plugins.google.gms.google.services)

}

android {
    namespace = "com.example.blinklit"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.example.blinklit"
        minSdk = 24
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        vectorDrawables.useSupportLibrary = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    signingConfigs {
        create("release") {
            storeFile = file("C:/Users/robbi/my-release-key.keystore")
            storePassword = "895484"
            keyAlias = "my-key-alias"
            keyPassword = "895484"
        }
    }

    buildTypes {
        getByName("release") {
            signingConfig = signingConfigs.getByName("release")
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    implementation(libs.androidx.activity)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.lottie)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.database)
    implementation(libs.glide)
    implementation(libs.firebase.vertexai)
    annotationProcessor(libs.compiler)
    implementation("com.razorpay:checkout:1.6.26")

    implementation(libs.volley)

    implementation (libs.checkout)

    implementation("com.facebook.shimmer:shimmer:0.5.0@aar")

    implementation("com.github.denzcoskun:ImageSlideshow:0.1.2")
    implementation(libs.firebase.storage)
    implementation(libs.androidx.games.activity)
    implementation(libs.androidx.legacy.support.v4)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
}
