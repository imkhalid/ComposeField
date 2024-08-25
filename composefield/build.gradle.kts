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
                            dependencyNode.appendNode(
                                "scope",
                                "compile"
                            ) // 'compile' for api
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