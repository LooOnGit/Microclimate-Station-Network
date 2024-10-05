pluginManagement {
    repositories {
        google()
        jcenter()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { url = uri("https://www.jitpack.io" ) }
        google()
        jcenter()
    }
}

rootProject.name = "LooFarm"
include(":app")
