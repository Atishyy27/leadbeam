pluginManagement {
    repositories {
        google()
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

rootProject.name = "FieldFlow"

include(":app")
include(":core:common")
include(":core:network")
include(":core:database")
include(":core:datastore")
include(":core:ui")
include(":feature:auth")
include(":feature:map")
include(":feature:business")
include(":feature:route")
include(":feature:profile")