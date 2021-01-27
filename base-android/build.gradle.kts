plugins {
    id(Libs.Plugins.androidLibrary)
    kotlin(Libs.Plugins.kotlinAndroid)
    kotlin(Libs.Plugins.kotlinKapt)
    kotlin(Libs.Plugins.kotlinAndroidExtensions)
}

android {

    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        minSdkVersion(AndroidSdk.min)

        testInstrumentationRunner = Libs.TestDependencies.testRunner

        consumerProguardFiles(
            file("proguard-rules.pro")
        )

        resConfigs(AndroidSdk.locales)
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    sourceSets {
        map { it.java.srcDirs("src/${it.name}/kotlin")}
    }

    testOptions {
        unitTests.apply {
            isReturnDefaultValues = true
            isIncludeAndroidResources = true
        }
    }

    buildFeatures.dataBinding = true

    packagingOptions {
        pickFirst ("META-INF/*")
    }
}

androidExtensions {
    isExperimental = true
}

dependencies {
    implementation(Libs.Kotlin.stdlib)

    api(Libs.AndroidX.Fragment.fragment)

    api(Libs.Coil.coil)

    api(Libs.AndroidX.recyclerview)

    api(Libs.Google.materialWidget)
    api(Libs.AndroidX.coreKtx)
    api(Libs.AndroidX.appcompat)
}