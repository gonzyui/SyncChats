plugins {
    kotlin("jvm") version "2.1.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "xyz.gonzyui.syncchats"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    implementation(kotlin("stdlib"))
    implementation(kotlin("reflect"))

    compileOnly("org.spigotmc:spigot-api:1.8.8-R0.1-SNAPSHOT")
    compileOnly("io.papermc.paper:paper-api:1.20-R0.1-SNAPSHOT")

    implementation("net.dv8tion:JDA:5.0.0-beta.12") {
        exclude(module = "opus-java")
    }

    implementation("com.neovisionaries:nv-websocket-client:2.14")
    implementation("com.squareup.okhttp3:okhttp:4.11.0")
    implementation("com.squareup.okio:okio:2.10.0")
    implementation("com.fasterxml.jackson.core:jackson-databind:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-core:2.15.2")
    implementation("com.fasterxml.jackson.core:jackson-annotations:2.15.2")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
    implementation("javax.annotation:javax.annotation-api:1.3.2")
    implementation("org.slf4j:slf4j-api:2.0.7")
    implementation("org.slf4j:slf4j-simple:2.0.7")
    implementation("org.apache.commons:commons-collections4:4.4")
    implementation("org.yaml:snakeyaml:2.0")

    implementation("net.sf.trove4j:trove4j:3.0.3")
}

configurations.all {
    resolutionStrategy {
        force("com.squareup.okio:okio:2.10.0")
        force("com.neovisionaries:nv-websocket-client:2.14")
        force("net.sf.trove4j:trove4j:3.0.3")
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")

        mergeServiceFiles()

        dependencies {
            include(dependency("net.dv8tion:JDA"))
            include(dependency("com.neovisionaries:nv-websocket-client"))
            include(dependency("com.squareup.okhttp3:okhttp"))
            include(dependency("com.squareup.okio:okio"))
            include(dependency("com.fasterxml.jackson.core:jackson-databind"))
            include(dependency("com.fasterxml.jackson.core:jackson-core"))
            include(dependency("com.fasterxml.jackson.core:jackson-annotations"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core"))
            include(dependency("javax.annotation:javax.annotation-api"))
            include(dependency("org.slf4j:slf4j-api"))
            include(dependency("org.slf4j:slf4j-simple"))
            include(dependency("org.apache.commons:commons-collections4"))
            include(dependency("net.sf.trove4j:trove4j"))
            include(dependency("org.yaml:snakeyaml"))
            include(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            include(dependency("org.jetbrains.kotlin:kotlin-reflect"))
        }
    }

    build {
        dependsOn(shadowJar)
    }
}

tasks.withType<org.jetbrains.kotlin.gradle.tasks.KotlinCompile> {
    kotlinOptions.jvmTarget = "17"
}

kotlin {
    jvmToolchain(17)
}