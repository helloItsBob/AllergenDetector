plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("androidx.navigation.safeargs.kotlin")
    id("com.google.gms.google-services")
    id("kotlin-android")
    id("kotlin-kapt")
}

android {
    namespace = "com.bpr.allergendetector"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.bpr.allergendetector"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

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
    buildFeatures {
        viewBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.6.2")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.6.2")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")
    implementation("com.google.firebase:firebase-firestore:24.9.1")
    //Testing
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.junit.jupiter:junit-jupiter:5.8.1")
    testImplementation("androidx.arch.core:core-testing:2.2.0")
    // Mockito
    testImplementation("org.mockito:mockito-core:3.11.2")
    testImplementation("org.mockito:mockito-inline:2.13.0")

    // AndroidJUnitRunner and JUnit Rules
    androidTestImplementation("androidx.test:runner:1.5.2")
    androidTestImplementation("androidx.test:rules:1.5.0")
    // Assertions
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    // Espresso dependencies
    androidTestImplementation( "androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation( "androidx.test.espresso:espresso-contrib:3.5.1"){
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation( "androidx.test.espresso:espresso-intents:3.5.1")
    androidTestImplementation( "androidx.test.espresso:espresso-accessibility:3.5.1"){
        exclude(module = "protobuf-lite")
    }
    androidTestImplementation( "androidx.test.espresso:espresso-web:3.5.1")
    androidTestImplementation( "androidx.test.espresso.idling:idling-concurrent:3.5.1")
    androidTestImplementation( "androidx.test.espresso:espresso-idling-resource:3.5.1")

    // cameraX dependencies
    val cameraxVersion = "1.3.0"
    implementation("androidx.camera:camera-core:${cameraxVersion}")
    implementation("androidx.camera:camera-camera2:${cameraxVersion}")
    implementation("androidx.camera:camera-lifecycle:${cameraxVersion}")
    implementation("androidx.camera:camera-view:${cameraxVersion}")
    implementation("androidx.camera:camera-extensions:${cameraxVersion}")

    // glide dependencies
    implementation("com.github.bumptech.glide:glide:4.12.0")
    //noinspection KaptUsageInsteadOfKsp
    kapt("com.github.bumptech.glide:compiler:4.12.0")

    // ML Kit dependencies
    implementation("com.google.mlkit:text-recognition:16.0.0")

    // Image cropper dependencies
    implementation("com.vanniktech:android-image-cropper:4.5.0")

    // Firebase dependencies
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")

    // Room dependencies
    val roomVersion = "2.6.0"
    implementation("androidx.room:room-ktx:$roomVersion")
    //noinspection KaptUsageInsteadOfKsp
    kapt("androidx.room:room-compiler:$roomVersion")
    androidTestImplementation("androidx.room:room-testing:$roomVersion")

    // material design
    implementation("com.google.android.material:material:1.10.0")

    // Gson
    implementation("com.google.code.gson:gson:2.9.0")
}