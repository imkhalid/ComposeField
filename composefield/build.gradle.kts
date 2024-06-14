val hiltWork by extra("1.2.0")
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("kotlin-kapt")
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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
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
            create<MavenPublication>("mavenJava") {
                // Adjust according to what you are publishing (e.g., 'java', 'androidRelease', etc.)
                from(components.getByName("release"))

                // Configure the POM file
                pom.withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    // Ensure all configurations that could contain dependencies are included
                    arrayOf("api", "implementation").forEach { configName ->
                        configurations[configName].allDependencies.forEach {
                            val dependencyNode = dependenciesNode.appendNode("dependency")
                            dependencyNode.appendNode("groupId", it.group)
                            dependencyNode.appendNode("artifactId", it.name)
                            dependencyNode.appendNode("version", it.version)
                            dependencyNode.appendNode("scope", "compile") // 'compile' for api exposure
                        }
                    }
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
    api(platform("androidx.compose:compose-bom:2024.04.01"))
    api("androidx.compose.ui:ui")
    api("androidx.compose.ui:ui-graphics")
    api("androidx.compose.ui:ui-tooling-preview")
    api("androidx.compose.material3:material3")
    implementation(project(":library"))

    implementation("androidx.hilt:hilt-navigation-compose:$hiltWork")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
}