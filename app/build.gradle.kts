val dagger2 by extra("2.50")
val hiltWork by extra("1.2.0")
plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("dagger.hilt.android.plugin")
    id("com.google.dagger.hilt.android") version "2.50" apply false
}

android {
    namespace = "com.techInfo.composefieldproject"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.techInfo.composefieldproject"
        minSdk = 24
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
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2024.04.01"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation(project(":composefield"))
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation(platform("androidx.compose:compose-bom:2024.04.01"))
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")

    implementation("com.google.dagger:hilt-android:$dagger2") //Hilt
    implementation ("androidx.hilt:hilt-work:$hiltWork")
    implementation ("androidx.hilt:hilt-common:$hiltWork")
    kapt ("com.google.dagger:hilt-android-compiler:$dagger2") //Hilt-Compiler
    kapt ("androidx.hilt:hilt-compiler:$hiltWork")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltWork")

    implementation("com.squareup.okhttp3:okhttp:4.11.0") // Use the latest version
    implementation("com.squareup.retrofit2:converter-gson:2.9.0") // Use the latest version

    implementation ("io.michaelrocks:libphonenumber-android:8.13.28") //Phone Number
    // "-keepresourcefiles assets/io/michaelrocks/libphonenumber/android/**"
}