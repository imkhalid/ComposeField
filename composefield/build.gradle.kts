val hiltWork by extra("1.2.0")
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("com.google.devtools.ksp")
    id ("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.imkhalid.composefield"
    compileSdk = 36

    defaultConfig {
        minSdk = 26
        targetSdk = 36
        multiDexEnabled = true

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            isShrinkResources =false
//            proguardFiles(
//                getDefaultProguardFile("proguard-android-optimize.txt"),
//                "proguard-rules.pro"
//            )
        }
    }
    buildFeatures {
        compose = true
    }

    kotlin {
        jvmToolchain(21)
    }

    java {
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

group = "com.github.imkhalid"
version = "1.2.3"

publishing {
    publications {
        register<MavenPublication>("release") {
            groupId = project.group.toString()
            artifactId = "composefield"
            version = project.version.toString()

            afterEvaluate {
                from(components["release"])
            }
        }
    }
    repositories {
        maven {
            name = "GitHubPackages"
            url = uri("https://maven.pkg.github.com/imkhalid/composefield")
            credentials {
                username = System.getenv("GITHUB_USERNAME") ?: ""  // GitHub username
                password = System.getenv("GITHUB_TOKEN") ?: ""     // Classic PAT with `write:packages`
            }
        }
    }
}




dependencies {

    implementation("androidx.core:core-ktx:1.16.0")
    implementation("androidx.multidex:multidex:2.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.1")
    implementation("androidx.activity:activity-compose:1.10.1")
    api(platform("androidx.compose:compose-bom:2026.06.01"))
    api("androidx.compose.ui:ui")
    api("androidx.compose.ui:ui-graphics")
    api("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material:material-icons-extended")
    api("androidx.compose.material3:material3:1.4.0")
//    api(project(":library"))
    implementation("com.googlecode.libphonenumber:libphonenumber:9.0.35")
    implementation("androidx.hilt:hilt-navigation-compose:$hiltWork")
    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")
    debugImplementation("androidx.compose.ui:ui-tooling:1.11.4")
}
