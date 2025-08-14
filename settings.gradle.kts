rootProject.name = "KasumiNotes"
include(":app", ":calendarview")

pluginManagement {
    repositories {
        google()
        mavenCentral()
        gradlePluginPortal()
        maven(url = "https://jitpack.io")
    }
}