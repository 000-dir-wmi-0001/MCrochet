plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.google.gms.google.services)
}

android {
    namespace = "com.example.thecrochetfactory"
    compileSdk = 34

   buildFeatures{
       viewBinding=true
   }

    defaultConfig {
        applicationId = "com.example.thecrochetfactory"
        minSdk = 24
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
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.firebase.auth)
    implementation(libs.firebase.firestore)
    implementation(libs.firebase.storage)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation("com.squareup.picasso:picasso:2.8")
    implementation("com.braintreepayments:card-form:5.4.0")
    implementation ("com.google.firebase:firebase-database:20.3.0")

    implementation("com.google.firebase:firebase-analytics")
    implementation(platform("com.google.firebase:firebase-bom:33.4.0"))
    implementation ("com.google.firebase:firebase-core:17.2.1")
    implementation ("com.google.firebase:firebase-auth:22.0.0")
    implementation("com.google.firebase:firebase-firestore:24.0.0")
    implementation ("androidx.appcompat:appcompat:1.3.1")
    implementation ("com.google.android.material:material:1.4.0")

    implementation ("com.google.firebase:firebase-storage:21.0.1") // Check for the latest version
    implementation ("com.github.bumptech.glide:glide:4.12.0") // Check for the latest version
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0")

//    implementation 'com.google.firebase:firebase-auth:22.0.0'
//    implementation 'com.google.firebase:firebase-firestore:24.0.0'
//    implementation 'com.google.firebase:firebase-storage:20.0.0'
    implementation ("com.google.android.gms:play-services-auth:20.0.0")
}
