plugins {
    id("com.android.application") version "8.9.1" apply false
    id("org.jetbrains.kotlin.android") version "2.0.21" apply false
}

tasks.register("clean", Delete::class) {
    delete(rootProject.buildDir)
}