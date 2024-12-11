plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "org.itmo"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    implementation("org.apache.hadoop:hadoop-common:3.4.0")
    implementation("org.apache.hadoop:hadoop-mapreduce-client-core:3.4.0")

    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(21)
}

application {
    mainClass.set("SalesMapReduceKt")
}

