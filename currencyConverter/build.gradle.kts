plugins {
    id(Libs.Plugins.androidLibrary)
    kotlin(Libs.Plugins.kotlinAndroid)
    kotlin(Libs.Plugins.kotlinAndroidExtensions)
    kotlin(Libs.Plugins.kotlinKapt)
    id(Libs.Plugins.kaptDagger)
    id(Libs.Plugins.kotlinNavigation)
}

android {
    compileSdkVersion(AndroidSdk.compile)

    kapt {
        arguments {
            hashMapOf(
                "room.schemaLocation" to "$projectDir/schemas",
                "room.incremental" to "true",
                "room.expandProjection" to "true"
            )
        }
    }

    defaultConfig {
        minSdkVersion(AndroidSdk.min)
        multiDexEnabled = true

        testInstrumentationRunner = "reprator.currencyconverter.CustomTestRunner"

        consumerProguardFiles(
            file("proguard-rules.pro")
        )

        resConfigs(AndroidSdk.locales)
        buildConfigField("String", AppConstant.buildConfigConstant_apiKey, "\"${propOrDef("PAYPAY_CURRENCYLAYER_API_KEY", "")}\"")
        buildConfigField("String", AppConstant.hostConstant, "\"${AppConstant.host}\"")
    }

    buildFeatures.dataBinding = true
    buildFeatures.viewBinding = true

    kotlinOptions {
        jvmTarget = JavaVersion.VERSION_1_8.toString()
    }

    sourceSets {
        map { it.java.srcDirs("src/${it.name}/kotlin") }
        getByName("androidTest").assets.srcDirs("$projectDir/schemas")
        getByName("androidTest").java.srcDirs("src/test-common/kotlin")
        getByName("test").java.srcDirs("src/test-common/kotlin")
    }

    testOptions {
        unitTests.isReturnDefaultValues = true
        unitTests.isIncludeAndroidResources = true
    }

    buildTypes {

        getByName("release") {
            isMinifyEnabled = true
            proguardFiles(
                getDefaultProguardFile("proguard-android.txt"),
                file("proguard-rules.pro")
            )
        }
    }

    packagingOptions {
        exclude ("META-INF/atomicfu.kotlin_module")
        pickFirst ("META-INF/*")
    }
}

androidExtensions {
    isExperimental = true
}

kapt {
    correctErrorTypes = true
    useBuildCache = true
    generateStubs = true
    javacOptions {
        option("-Xmaxerrs", 500)
    }
}

dependencies {
    implementation(project(AppModules.moduleBase))
    implementation(project(AppModules.moduleNavigation))
    implementation(project(AppModules.moduleBaseAndroid))

    implementation(Libs.AndroidX.multidex)

    implementation(Libs.AndroidX.annotation)
    implementation(Libs.AndroidX.recyclerview)
    implementation(Libs.AndroidX.cardView)
    implementation(Libs.AndroidX.constraintlayout)

    implementation(Libs.Kotlin.stdlib)
    implementation(Libs.Coroutines.core)

    implementation(Libs.AndroidX.Lifecycle.livedata)

    implementation(Libs.AndroidX.Navigation.fragmentKtx)

    implementation(Libs.AndroidX.Fragment.fragment)
    implementation(Libs.AndroidX.Fragment.fragmentKtx)

    implementation(Libs.AndroidX.Room.runtime)
    implementation(Libs.AndroidX.Room.ktx)
    kapt(Libs.AndroidX.Room.compiler)

    implementation(Libs.Retrofit.retrofit)
    implementation(Libs.Retrofit.jacksonConverter)

    //Hilt
    implementation(Libs.DaggerHilt.hilt)
    kapt(Libs.DaggerHilt.hiltCompilerAndroid)

    //Hilt WorkManager
    implementation(Libs.AndroidX.Work.runtimeKtx)
    implementation(Libs.DaggerHilt.work)

    //ViewModal
    implementation(Libs.DaggerHilt.viewModel)
    kapt(Libs.DaggerHilt.hiltCompiler)

    implementation(Libs.Coroutines.coroutineTest)

    // Testing
    testImplementation(Libs.Coroutines.coroutineTest)
    testImplementation(Libs.TestDependencies.truth)
    testImplementation(Libs.TestDependencies.core)
    testImplementation(Libs.OkHttp.mockWebServer)
    testImplementation(Libs.TestDependencies.jUnit)
    testImplementation(Libs.TestDependencies.Mockk.unitTest)

    // Android Testing
    androidTestImplementation(Libs.TestDependencies.truth)
    androidTestImplementation(Libs.AndroidX.Room.test)
    androidTestImplementation(Libs.TestDependencies.extJUnit)
    testImplementation(Libs.TestDependencies.Mockk.instrumentedTest)

    //Hilt
    androidTestImplementation(Libs.DaggerHilt.hiltAndroidTest)
    kaptAndroidTest(Libs.DaggerHilt.hiltCompilerAndroid)

    //workmanager
    androidTestImplementation(Libs.AndroidX.Work.test)
    
    // AndroidX test
    androidTestImplementation(Libs.TestDependencies.AndroidXTestInstrumented.core)
    androidTestImplementation(Libs.TestDependencies.AndroidXTestInstrumented.runner)

    // Architecture components testing
    androidTestImplementation(Libs.TestDependencies.core)
}

fun propOrDef(propertyName: String, defaultValue: Any): Any {
    val propertyValue = project.properties[propertyName]
    return propertyValue ?: defaultValue
}