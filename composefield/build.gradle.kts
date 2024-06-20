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
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.1"
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}

//afterEvaluate {
//    publishing {
//        publications {
//            create<MavenPublication>("mavenJava") {
//                from(components["release"])
//
//                groupId = "com.github.imkhalid"
//                artifactId = "ComposeField"
//                version = "1.0.15" // You should dynamically set this or match your actual versioning
//
//                // Configure the POM file
//                pom {
//                    name.set("ComposeField")
//                    description.set("A Library to create all type of Fields base on Module supplied")
//                    url.set("https://github.com/imkhalid/ComposeField")
//
//                    scm {
//                        connection.set("scm:git:git://github.com/imkhalid/ComposeField.git")
//                        developerConnection.set("scm:git:ssh://github.com:imkhalid/ComposeField.git")
//                        url.set("https://github.com/imkhalid/ComposeField")
//                    }
//
//                    licenses {
//                        license {
//                            name.set("The Apache License, Version 2.0")
//                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
//                        }
//                    }
//
//                    developers {
//                        developer {
//                            id.set("imkhalid")
//                            name.set("Khalid Saeed")
//                            email.set("your_email@example.com")
//                        }
//                    }
//
//                    withXml {
//                        val dependenciesNode = asNode().appendNode("dependencies")
//                        configurations["implementation"].allDependencies.forEach {
//                            if (it.group != null && it.version != null) {
//                                val dependencyNode = dependenciesNode.appendNode("dependency")
//                                dependencyNode.appendNode("groupId", it.group)
//                                dependencyNode.appendNode("artifactId", it.name)
//                                dependencyNode.appendNode("version", it.version)
//                                dependencyNode.appendNode("scope", "compile")
//                            }
//                        }
//                    }
//                }
//            }
//        }
//        repositories {
//            mavenLocal()
//        }
//    }
//}


dependencies {

    implementation("androidx.core:core-ktx:1.13.0")
    implementation("androidx.multidex:multidex:2.0.1")
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
//    api(project(":library"))

    implementation("androidx.hilt:hilt-navigation-compose:$hiltWork")

    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.9.0")
}