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
    compileSdk = 35

    defaultConfig {
        minSdk = 26
        targetSdk = 35
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
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_21
        targetCompatibility = JavaVersion.VERSION_21
    }
}

group = "com.github.imkhalid" // ✅ Required for JitPack
version = "1.0.16" // ✅ Must match Git tag

afterEvaluate {
    publishing {
        publications {
            create<MavenPublication>("jitpack") {
                groupId = "com.github.imkhalid" // ✅ Required for JitPack
                artifactId = "composefield" // ✅ Explicitly set `artifactId`
                version = "1.0.16"

                // ✅ Use correct Android component
                if (components.findByName("release") != null) {
                    from(components["release"])
                } else {
                    throw GradleException("❌ ERROR: 'release' component not found. Check if the correct component is being published.")
                }

                pom {
                    name.set("ComposeField")
                    description.set("A library for handling form fields in Jetpack Compose.")
                    url.set("https://github.com/imkhalid/composefield")

                    licenses {
                        license {
                            name.set("Apache-2.0")
                            url.set("https://opensource.org/licenses/Apache-2.0")
                        }
                    }

                    developers {
                        developer {
                            id.set("imkhalid")
                            name.set("Khalid")
                            email.set("khalidsaeed36@gmail.com")
                        }
                    }

                    scm {
                        url.set("https://github.com/imkhalid/composefield")
                        connection.set("scm:git:git://github.com/imkhalid/composefield.git")
                        developerConnection.set("scm:git:ssh://github.com/imkhalid/composefield.git")
                    }
                }
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

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.9.0")
    implementation("androidx.activity:activity-compose:1.10.1")
    api(platform("androidx.compose:compose-bom:2025.05.01"))
    api("androidx.compose.ui:ui")
    api("androidx.compose.ui:ui-graphics")
    api("androidx.compose.ui:ui-tooling-preview")
    api("androidx.compose.material3:material3:1.3.2")
//    api(project(":library"))

    implementation("androidx.hilt:hilt-navigation-compose:$hiltWork")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.1.20")
    debugImplementation("androidx.compose.ui:ui-tooling:1.8.2")
}