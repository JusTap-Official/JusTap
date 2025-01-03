plugins {
    id ("com.android.application")
    id ("org.jetbrains.kotlin.android")
    id ("com.google.gms.google-services")
    id ("kotlin-parcelize")
    id ("androidx.navigation.safeargs.kotlin")
    id ("com.google.firebase.crashlytics")
    id ("kotlin-kapt")
    id("com.google.dagger.hilt.android")
    id ("com.google.firebase.firebase-perf")
}

android {
    namespace = "com.binay.shaw.justap"
    compileSdk = 33

    defaultConfig {
        applicationId = "com.binay.shaw.justap"
        minSdk = 21
        targetSdk = 33
        versionCode = 7
        versionName = "2.1.2"
        vectorDrawables.useSupportLibrary = true
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        viewBinding = true
        buildConfig  = true
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles ( getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro" )
        }
        getByName("debug") {
            isDebuggable = true
        }
    }

    lint {
        abortOnError = false
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    val lifecycleVersion = "2.6.1"
    val cameraX = "1.3.0-alpha05"
    val navVersion = "2.5.3"
    val roomVersion = "2.5.1"
    val lottieVersion = "5.2.0"

    //Default Pre-defined
    implementation ("androidx.core:core-ktx:1.9.0")
    implementation ("androidx.appcompat:appcompat:1.6.1")
    implementation ("com.google.android.material:material:1.8.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    //Test
    testImplementation ("junit:junit:4.13.2")
    androidTestImplementation ("androidx.test.ext:junit:1.1.5")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")
    testImplementation ("com.google.truth:truth:1.1.3")
    androidTestImplementation ("androidx.test:runner:1.5.2")
    androidTestImplementation ("androidx.test:rules:1.5.0")
    androidTestImplementation ("com.google.dagger:hilt-android-testing:2.48")
    kaptAndroidTest("com.google.dagger:hilt-android-compiler:2.48")


    //Navigation
    implementation ("androidx.navigation:navigation-fragment-ktx:$navVersion")
    implementation ("androidx.navigation:navigation-ui-ktx:$navVersion")

    //LottieFiles Animation
    implementation ("com.airbnb.android:lottie:$lottieVersion")

    //ViewModel
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:$lifecycleVersion")

    // LiveData
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:$lifecycleVersion")

    //Firebase
    implementation (platform("com.google.firebase:firebase-bom:31.1.1"))
    implementation ("com.google.firebase:firebase-auth-ktx:21.2.0")
    implementation ("com.google.firebase:firebase-database-ktx:20.1.0")
    implementation ("com.google.firebase:firebase-storage-ktx:20.1.0")
    implementation ("com.google.android.gms:play-services-auth:20.4.1")
    implementation ("com.google.firebase:firebase-crashlytics-ktx")
    implementation ("com.google.firebase:firebase-analytics-ktx")
    implementation ("com.google.firebase:firebase-perf-ktx")
    implementation ("com.google.firebase:firebase-messaging-ktx:23.2.1")


    //Room Database
    implementation("androidx.room:room-runtime:$roomVersion")
    annotationProcessor("androidx.room:room-compiler:$roomVersion")
    kapt("androidx.room:room-compiler:$roomVersion")
    implementation("androidx.room:room-ktx:$roomVersion")

    //Coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-play-services:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.6.4")

    //QR Code generator
    implementation ("com.google.zxing:core:3.5.1")

    //ML Kit
    implementation ("com.google.mlkit:barcode-scanning:17.1.0")

    //CameraX
    implementation ("androidx.camera:camera-camera2:$cameraX")
    implementation ("androidx.camera:camera-lifecycle:$cameraX")
    implementation ("androidx.camera:camera-view:$cameraX")

    //ImagePicker
    implementation ("com.github.dhaval2404:imagepicker:2.1")
    implementation ("androidx.legacy:legacy-support-v4:1.0.0")

    //Glide
    implementation ("com.github.bumptech.glide:glide:4.14.2")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.14.2")

    //Circular ImageView
    implementation ("de.hdodenhof:circleimageview:3.1.0")

    //Custom AlertBar (Top)
    implementation ("com.github.tapadoo:alerter:7.2.4")

    //ColorPicker
    implementation ("com.github.skydoves:colorpickerpreference:2.0.6")

    // In-app update
    implementation ("com.google.android.play:core:1.10.3")

    //DaggerHilt
    implementation("com.google.dagger:hilt-android:2.48")
    kapt("com.google.dagger:hilt-compiler:2.48")
}