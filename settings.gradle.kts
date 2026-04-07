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
plugins {
    id("org.gradle.toolchains.foojay-resolver-convention") version "1.0.0"
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        google()
        mavenCentral()
    }
}

rootProject.name = "crm"
include(":app")
include(":uikit")
include(":core:database")
include(":core:domain")
include(":feature:settings:api")
include(":feature:settings:impl")
include(":feature:notes:api")
include(":feature:notes:impl")
include(":feature:tasks:api")
include(":feature:tasks:impl")
include(":feature:chat:api")
include(":feature:chat:impl")
include(":feature:profile:api")
include(":feature:profile:impl")
