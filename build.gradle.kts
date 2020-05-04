import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    java
    kotlin("jvm") version "1.3.72"
}

group = "org.example.adventofcode2019"
version = "1.0-SNAPSHOT"

allprojects {
    repositories {
        mavenCentral()
        jcenter()
    }
}

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("org.jetbrains.kotlinx", "kotlinx-coroutines-core", "1.3.5")
    implementation("com.google.guava", "guava", "29.0-jre")
    compile(project(":intcode"))
    compile(project(":grid"))
    compile(project(":util"))
    testCompile("junit", "junit", "4.12")
}


configure<JavaPluginConvention> {
    sourceCompatibility = JavaVersion.VERSION_12
}
tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}
val compileKotlin: KotlinCompile by tasks
compileKotlin.kotlinOptions {
    freeCompilerArgs = listOf("-Xinline-classes")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

dependencies {
    val kotestVersion = "4.0.5"
    testImplementation("io.kotest", "kotest-runner-junit5-jvm", kotestVersion) // for kotest framework
    testImplementation("io.kotest", "kotest-assertions-core-jvm", kotestVersion) // for kotest core jvm assertions
    testImplementation("io.kotest", "kotest-property-jvm", kotestVersion) // for kotest property test
}