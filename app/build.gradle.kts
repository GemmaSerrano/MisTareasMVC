plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.miappnivel5"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.miappnivel5"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false //se es true cambia nombres de retrofit y produce errores
                packageScope = setOf("androidx.**", "kotlin.**", "kotlinx.**")
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
}

dependencies {
    implementation(libs.activity.ktx)
    implementation(libs.appcompat)
    implementation(libs.constraintlayout)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.espresso.core)
    androidTestImplementation(libs.ext.junit)


    implementation(libs.gson)

    implementation(libs.retrofit)
    implementation(libs.retrofit.converter.gson)

}
