plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.gms.google-services")
}


android {
    namespace = "com.example.loofarm"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.loofarm"
        minSdk = 24
        targetSdk = 33
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

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("com.google.firebase:firebase-auth:22.2.0")
    implementation("com.google.android.gms:play-services-auth:20.7.0")
    implementation("androidx.privacysandbox.tools:tools-core:1.0.0-alpha09")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

//    add libs
    implementation(platform("com.google.firebase:firebase-bom:32.4.1"))
    implementation("com.google.firebase:firebase-database-ktx")
    implementation("androidx.cardview:cardview:1.0.0")
    implementation("androidx.recyclerview:recyclerview:1.3.2")

    //add chart
    implementation("com.github.PhilJay:MPAndroidChart:v3.1.0")

    //qr
    implementation("com.github.yuriy-budiyev:code-scanner:2.1.0")

    //mqtt
//    implementation("org.eclipse.paho:org.eclipse.paho.client.mqttv3:1.2.4")
//    implementation("org.eclipse.paho:org.eclipse.paho.android.service:1.1.1")

    //thingspeak
    implementation("com.android.volley:volley:1.2.1")
}