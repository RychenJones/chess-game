plugins {
    kotlin("jvm") version "2.4.20"
    id("org.jetbrains.compose") version "1.12.2"
    id("org.jetbrains.kotlin.plugin.compose") version "2.4.20"
}

group = "chess.game"
version = "1.0-SNAPSHOT"

kotlin {
    jvmToolchain(25)
}

sourceSets {
    main {
        kotlin.srcDirs("src")
        kotlin.exclude("test/**")
    }
    test {
        kotlin.srcDirs("src/test")
    }
}

dependencies {
    implementation(compose.desktop.currentOs)
    implementation(compose.material3)
    testImplementation(kotlin("test"))
}

compose.desktop {
    application {
        mainClass = "MainKt"
    }
}

tasks.test {
    useJUnitPlatform()
}
