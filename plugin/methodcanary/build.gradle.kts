plugins {
    `java-gradle-plugin`
    alias(libs.plugins.kotlin.jvm)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17))
    }
}


dependencies {
    compileOnly(libs.android.gradlePlugin.api)
    implementation(gradleKotlinDsl())

    implementation( "org.ow2.asm:asm:7.1")
    implementation( "org.ow2.asm:asm-commons:7.1")
    implementation( "org.ow2.asm:asm-util:7.1")
    implementation( "commons-io:commons-io:2.11.0")
    implementation ( "commons-codec:commons-codec:1.17.1")
}

gradlePlugin {
    plugins {
        create("customPlugin") {
            id = "cn.hikyson.methodcanary.plugin"
            implementationClass = "cn.hikyson.methodcanary.plugin.MethodCanaryPlugin"
        }
    }
}

//uploadArchives {
//    repositories {
//        mavenDeployer {
//            pom.groupId = 'cn.hikyson.methodcanary'
//            pom.artifactId = 'plugin'
//            pom.version = 0.1
//            repository(url: uri('../repos'))
//        }
//    }
//}

//apply from: rootProject.file('gradle/gradleplugin-jcenter-push.gradle')
