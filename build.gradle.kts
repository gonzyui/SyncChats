plugins {
    kotlin("jvm") version "2.1.10"
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "xyz.gonzyui"
version = "1.0.4"

repositories {
    mavenCentral()
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/")
    maven("https://oss.sonatype.org/content/repositories/snapshots")
    maven("https://m2.dv8tion.net/releases")
}

dependencies {
    compileOnly(kotlin("stdlib"))
    compileOnly(kotlin("reflect"))
    compileOnly("org.spigotmc:spigot-api:1.20.1-R0.1-SNAPSHOT")
    compileOnly("org.json:json:20230227")
    compileOnly("javax.annotation:javax.annotation-api:1.3.2")
    
    implementation("net.dv8tion:JDA:5.0.0-beta.12") {
        exclude(module = "opus-java")
    }
    
    implementation("com.neovisionaries:nv-websocket-client:2.14")
    implementation("com.squareup.okhttp3:okhttp:4.10.0")
    implementation("com.squareup.okio:okio:3.0.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.6.4")
}

configurations.all {
    resolutionStrategy {
        force("com.neovisionaries:nv-websocket-client:2.14")
        force("com.squareup.okhttp3:okhttp:4.10.0")
        force("com.squareup.okio:okio:3.0.0")
    }
}

tasks {
    shadowJar {
        archiveClassifier.set("")
        archiveFileName.set("SyncChats-${version}.jar")

        exclude("META-INF/*.RSA", "META-INF/*.SF", "META-INF/*.DSA")
        exclude("**/*.md", "**/*.txt", "**/*.properties")
        exclude("**/module-info.class")
        exclude("**/MANIFEST.MF")
        exclude("**/LICENSE*")
        exclude("**/NOTICE*")
        exclude("**/README*")
        exclude("**/*.kotlin_metadata")
        exclude("**/*.kotlin_builtins")
        exclude("**/*.proto")

        mergeServiceFiles()

        dependencies {
            include(dependency("net.dv8tion:JDA"))
            include(dependency("com.neovisionaries:nv-websocket-client"))
            include(dependency("com.squareup.okhttp3:okhttp"))
            include(dependency("com.squareup.okio:okio"))
            include(dependency("com.squareup.okio:okio-jvm"))
            include(dependency("org.apache.commons:commons-collections4"))
            include(dependency("net.sf.trove4j:trove4j"))
            include(dependency("org.jetbrains.kotlinx:kotlinx-coroutines-core"))
            
            exclude(dependency("org.jetbrains.kotlin:kotlin-stdlib"))
            exclude(dependency("org.jetbrains.kotlin:kotlin-reflect"))
            exclude(dependency("org.json:json"))
            exclude(dependency("javax.annotation:javax.annotation-api"))
        }

        minimize {
            exclude(dependency("net.dv8tion:JDA"))
            exclude(dependency("com.squareup.okhttp3:okhttp"))
            exclude(dependency("com.squareup.okio:okio"))
            exclude(dependency("com.squareup.okio:okio-jvm"))
            exclude(dependency("org.apache.commons:commons-collections4"))
            exclude(dependency("net.sf.trove4j:trove4j"))
        }
        
        relocate("kotlinx.coroutines", "xyz.gonzyui.syncchats.libs.coroutines")
        relocate("com.neovisionaries", "xyz.gonzyui.syncchats.libs.websocket")
    }

    build {
        dependsOn(shadowJar)
    }
}

kotlin {
    jvmToolchain(17)
}
