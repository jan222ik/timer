import org.jetbrains.compose.desktop.application.dsl.TargetFormat

plugins {
    kotlin("multiplatform")
    id("org.jetbrains.compose")
}

group = "fyi.mayr"
version = "1.0-SNAPSHOT"

repositories {
    google()
    mavenCentral()
    maven("https://maven.pkg.jetbrains.space/public/p/compose/dev")
}

kotlin {
    jvm {
        jvmToolchain(17)
        withJava()
    }
    sourceSets {
        val jvmMain by getting {
            dependencies {
                implementation(compose.desktop.currentOs)
                implementation(files("libs/new-ui-desktop-0.0.1-SNAPSHOT.jar"))
                implementation(files("libs/core-0.0.1-SNAPSHOT.jar"))
                implementation(files("libs/compose-utils-0.0.1-SNAPSHOT.jar"))
                implementation(files("libs/new-ui-standalone-0.0.1-SNAPSHOT.jar"))
            }
        }
        val jvmTest by getting
    }
}

compose.desktop {
    application {
        mainClass = "MainKt"
        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = "timer"
            packageVersion = "1.0.0"
        }
    }
}
