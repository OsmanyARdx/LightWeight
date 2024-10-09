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
    versionCatalogs {
        create("libs") {
            library("retrofit", "com.squareup.retrofit2:retrofit:2.9.0")
            library("gson", "com.squareup.retrofit2:converter-gson:2.9.0")
            library("lifecycle-runtime-ktx", "androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
            library("core-ktx", "androidx.core:core-ktx:1.10.0")
            library("activity-compose", "androidx.activity:activity-compose:1.7.0")
            library("material3", "androidx.compose.material3:material3:1.0.1")
        }
    }
}

rootProject.name = "LightWeight"
include(":app")
 