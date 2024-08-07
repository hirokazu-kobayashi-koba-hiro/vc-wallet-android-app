// Top-level build file where you can add configuration options common to all sub-projects/modules.
plugins {
    id("com.android.application") version "8.4.2" apply false
    id("org.jetbrains.kotlin.android") version "1.9.23" apply false
    id("com.android.library") version "8.4.2" apply false
    id("com.diffplug.spotless") version "6.25.0" apply true
    id("com.google.devtools.ksp") version "1.9.23-1.0.20" apply false
    kotlin("plugin.serialization") version "1.8.22"
}

spotless {

}