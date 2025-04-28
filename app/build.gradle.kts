plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("com.google.gms.google-services")
}

android {
    namespace = "com.example.waco"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.waco"
        minSdk = 31
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }

    buildFeatures {
        compose = true
        viewBinding = true
    }

    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }

    packagingOptions {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":opencv"))

    // Core
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    // Compose
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)

    // XML-based UI
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("androidx.navigation:navigation-ui-ktx:2.5.2")
    implementation("com.google.firebase:firebase-messaging")
    implementation("com.google.firebase:firebase-auth")

    // Firebase BOM
    implementation(platform("com.google.firebase:firebase-bom:32.0.0"))

    // Networking
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")

    // Other
    implementation(libs.identity.credential)
    implementation(libs.glide)
    implementation("org.json:json:20210307")
    implementation("androidx.security:security-crypto:1.1.0-alpha03")
    implementation("com.google.android.gms:play-services-maps:18.1.0")
    implementation("jp.wasabeef:glide-transformations:4.3.0")
    implementation ("androidx.activity:activity-ktx:1.8.0")
    implementation ("androidx.core:core-ktx:1.12.0")
    implementation ("com.github.bumptech.glide:glide:4.15.1")
    implementation ("com.squareup.okhttp3:okhttp:4.11.0")

    // CameraX
    implementation("androidx.camera:camera-core:1.3.0")
    implementation("androidx.camera:camera-camera2:1.3.0")
    implementation("androidx.camera:camera-lifecycle:1.3.0")
    implementation("androidx.camera:camera-view:1.3.0")
    implementation ("org.tensorflow:tensorflow-lite:2.14.0") // lub nowsze
    implementation ("org.tensorflow:tensorflow-lite-task-vision:0.4.1")
    // ✅ OpenCV (tylko jedno - przez libs)

    // Volley
    implementation(libs.volley)
    implementation(libs.litert.metadata)

    // Tests
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
}

apply(plugin = "com.google.gms.google-services")
