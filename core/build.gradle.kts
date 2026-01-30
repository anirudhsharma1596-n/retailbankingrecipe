plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
}

android {
    namespace = "com.axoid.retailbankingdummy.core"
    compileSdk = 36

    defaultConfig {
        minSdk = 24

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    implementation("androidx.security:security-crypto-ktx:1.1.0")
    // Retrofit for type-safe HTTP calls
    implementation("com.squareup.retrofit2:retrofit:2.9.0")

    // OkHttp client - Retrofit uses this under the hood. We need it for pinning.
    implementation("com.squareup.okhttp3:okhttp:4.12.0")

    // OkHttp logging interceptor for debugging network calls
    implementation("com.squareup.okhttp3:logging-interceptor:4.12.0")

    // Gson converter for JSON serialization/deserialization
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
}