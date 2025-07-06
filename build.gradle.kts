import kr.entree.spigradle.kotlin.*

plugins {
    kotlin("jvm") version "2.0.0"
    id("kr.entree.spigradle") version "2.4.6"
    id("com.gradleup.shadow") version "9.0.0-rc1"
}

group = "pe.chalk.bukkit"
version = "1.3.0"

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(spigot("1.21.4"))
    implementation(bStats("3.0.2"))
}

spigot {
    apiVersion = "1.21"
    description = "gksdud implementation for Minecraft servers"
}

tasks.shadowJar {
    archiveClassifier = ""
    relocate("org.bstats", "pe.chalk.bukkit.bstats")
}