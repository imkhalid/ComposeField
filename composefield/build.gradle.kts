val hiltWork by extra("1.2.0")
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
}

ext {
    val PUBLISH_GROUP_ID = "com.imkhalid"
    val PUBLISH_ARTIFACT_ID = "composeField"
    val PUBLISH_VERSION = "1.0.0"
}

android {
    namespace = "com.imkhalid.composefield"
    compileSdk = 34

    defaultConfig {
        minSdk = 24
        targetSdk = 34

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    kotlinOptions {
        jvmTarget = "1.8"
    }
}

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("aar") {
                groupId = "com.imkhalid"
                // The version will be provided by the build environment
                artifactId = project.name

                // Tell maven to prepare the generated "*.aar" file for publishing
                artifact("${buildDir}/outputs/aar/${project.name}-release.aar") {
                    builtBy(tasks.named("assembleRelease"))
                }
            }
        }
    }


}


dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.7.0")
    implementation("androidx.activity:activity-compose:1.9.0")
    implementation(platform("androidx.compose:compose-bom:2023.08.00"))
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")

    implementation ("io.michaelrocks:libphonenumber-android:8.13.28") //Phone Number

    implementation("androidx.hilt:hilt-navigation-compose:$hiltWork")
}