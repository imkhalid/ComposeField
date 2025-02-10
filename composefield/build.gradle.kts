val hiltWork by extra("1.2.0")
plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("maven-publish")
    id("org.jetbrains.kotlin.kapt")
    id ("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.imkhalid.composefield"
    compileSdk = 35

    defaultConfig {
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
afterEvaluate {
    publishing {
        publications {
            // ✅ Ensure AAR publication is configured correctly
            create<MavenPublication>("aar") {
                groupId = "com.imkhalid"
                artifactId = project.name
                version = "1.0.16" // ✅ Define version explicitly

                // Publish AAR
                artifact("${buildDir}/outputs/aar/${project.name}-release.aar") {
                    builtBy(tasks.named("assembleRelease"))
                }
            }

            // ✅ Ensure Java/Android publication is properly defined
            create<MavenPublication>("mavenJava") {
                groupId = "com.imkhalid"
                artifactId = project.name
                version = "1.0.16" // ✅ Define version explicitly

                // Ensure "release" component exists
                if (components.findByName("release") != null) {
                    from(components.getByName("release"))
                } else {
                    throw GradleException("❌ ERROR: 'release' component not found. Check if the correct component is being published.")
                }

                // ✅ Configure POM with safe dependency resolution
                pom.withXml {
                    val dependenciesNode = asNode().appendNode("dependencies")
                    listOf("api", "implementation").forEach { configName ->
                        configurations.findByName(configName)?.dependencies?.forEach { dep ->
                            if (dep.group != null && dep.name != null && dep.version != null) {
                                val dependencyNode = dependenciesNode.appendNode("dependency")
                                dependencyNode.appendNode("groupId", dep.group)
                                dependencyNode.appendNode("artifactId", dep.name)
                                dependencyNode.appendNode("version", dep.version)
                                dependencyNode.appendNode("scope", if (configName == "api") "compile" else "runtime")
                            } else {
                                println("⚠️ WARNING: Skipping dependency $dep because it has missing values.")
                            }
                        }
                    }
                }
            }
        }
    }
}



dependencies {

    implementation("androidx.core:core-ktx:1.13.1")
    implementation("androidx.multidex:multidex:2.0.1")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")

    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.4")
    implementation("androidx.activity:activity-compose:1.9.1")
    api(platform("androidx.compose:compose-bom:2024.08.00"))
    api("androidx.compose.ui:ui")
    api("androidx.compose.ui:ui-graphics")
    api("androidx.compose.ui:ui-tooling-preview")
    api("androidx.compose.material3:material3")
//    api(project(":library"))

    implementation("androidx.hilt:hilt-navigation-compose:$hiltWork")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:2.0.0")
}