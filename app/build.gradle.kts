plugins {
    id(Libs.Plugins.androidApplication)
    kotlin(Libs.Plugins.kotlinAndroid)
    kotlin(Libs.Plugins.kotlinKapt)
    id(Libs.Plugins.kotlinNavigation)
    id(Libs.Plugins.kaptDagger)
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    javacOptions {
        option("-Xmaxerrs", 500)
    }
}

android {
    compileSdkVersion(AndroidSdk.compile)

    defaultConfig {
        applicationId = AppConstant.applicationPackage

        minSdkVersion(AndroidSdk.min)
        targetSdkVersion(AndroidSdk.target)

        versionCode = AppVersion.versionCode
        versionName = AppVersion.versionName

        testInstrumentationRunner = Libs.TestDependencies.testRunner

        multiDexEnabled = true

        resConfigs(AndroidSdk.locales)

        buildConfigField("String", AppConstant.hostConstant, "\"${AppConstant.host}\"")
    }

    flavorDimensions(AppConstant.flavourDimension)

    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    sourceSets {
        map { it.java.srcDirs("src/${it.name}/kotlin") }
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    packagingOptions {
        pickFirst ("META-INF/*")
    }

}

dependencies {
    implementation(project(AppModules.moduleBase))
    implementation(project(AppModules.moduleBaseAndroid))
    implementation(project(AppModules.moduleNavigation))

    implementation(project(AppModules.moduleCurrencyConverter))

    implementation(Libs.AndroidX.appcompat)

    implementation(Libs.AndroidX.annotation)
    implementation(Libs.AndroidX.coreKtx)
    implementation(Libs.AndroidX.constraintlayout)

    kapt(Libs.AndroidX.Lifecycle.compiler)
    implementation(Libs.AndroidX.Lifecycle.extensions)

    implementation(Libs.AndroidX.Navigation.fragmentKtx)
    implementation(Libs.AndroidX.Navigation.uiKtx)

    implementation(Libs.AndroidX.multidex)

    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Coroutines.core)

    implementation(Libs.OkHttp.loggingInterceptor)

    //Hilt
    implementation(Libs.DaggerHilt.hilt)
    kapt(Libs.DaggerHilt.hiltCompilerAndroid)

    //Hilt WorkManager
    implementation(Libs.DaggerHilt.work)
    kapt(Libs.DaggerHilt.hiltCompiler)

    //ViewModal
    implementation(Libs.DaggerHilt.viewModel)

    testImplementation(Libs.TestDependencies.Mockk.unitTest)
    testImplementation(Libs.TestDependencies.truth)
}