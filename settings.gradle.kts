pluginManagement {
    repositories {
        google {
            content {
                includeGroupByRegex("com\\.android.*")
                includeGroupByRegex("com\\.google.*")
                includeGroupByRegex("androidx.*")
            }
        }
        mavenCentral()
        gradlePluginPortal()
    }
}

dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}
rootProject.name = "swemo"

enableFeaturePreview("TYPESAFE_PROJECT_ACCESSORS")
include(":app")
include(":database")
include(":designsystem")
include(":common")
include(":datastore")
include(":data")
include(":feature:test")
include(":model")
