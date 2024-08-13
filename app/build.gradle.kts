
plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("cn.hikyson.methodcanary.plugin")
}

//if (!Boolean.parseBoolean(DISABLE_PLUGIN)) {
//    apply plugin: 'cn.hikyson.methodcanary.plugin'
    AndroidGodEye {
        enableLifecycleTracer = true
        enableMethodTracer = true
        instrumentationRuleFilePath = "AndroidGodEye-MethodCanary.js"
        instrumentationRuleIncludeClassNamePrefix(listOf("cn/hikyson/methodcanary/sample"))
    }
//}

android {
    namespace = "cn.hikyson.methodcanary.sample"
    compileSdk = 34
    defaultConfig {
        minSdk = 30
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"
        testInstrumentationRunner = "android.support.test.runner.AndroidJUnitRunner"
    }

}
java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}
dependencies {
//    implementation(fileTree(dir='libs', include(['*.jar'])) )
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk7:1.9.20")
    implementation ("com.android.support:appcompat-v7:28.0.0")
    implementation ( "com.android.support.constraint:constraint-layout:1.1.3")
    implementation ( "com.android.support:design:28.0.0")
    testImplementation ( "junit:junit:4.12")
    androidTestImplementation ("com.android.support.test:runner:1.0.2")
    androidTestImplementation ( "com.android.support.test.espresso:espresso-core:3.0.2")
    implementation(project(":samplelib"))
    implementation ("com.orhanobut:logger:2.2.0")
    implementation (project(":lib"))
}
