rootProject.name = "MethodCanary"

pluginManagement {
    includeBuild("plugin")
    repositories {
        maven { url = uri("") }
        gradlePluginPortal()
        google()
        mavenCentral()
    }
}

dependencyResolutionManagement {
    repositories {
        maven { url = uri("") }
        google()
        mavenCentral()
    }
}

include (":app", ":samplelib",  ":lib")
