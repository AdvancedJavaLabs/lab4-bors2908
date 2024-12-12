plugins {
    kotlin("jvm") version "1.9.20"
    application
}

group = "org.itmo"

private val mainClassName: String = "org.itmo.SalesMapReduce"

application {
    mainClass = mainClassName
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.slf4j:slf4j-api:2.0.12")
    implementation("org.slf4j:slf4j-simple:2.0.13")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8")

    implementation("org.apache.hadoop:hadoop-common:3.4.0")
    implementation("org.apache.hadoop:hadoop-mapreduce-client-core:3.4.0")

    testImplementation("org.assertj:assertj-core:3.26.3")
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(8)
}

tasks.jar {
    manifest {
        attributes["Main-Class"] = "${mainClassName}Kt"
    }
}

tasks.register<Jar>("fatJar") {
    archiveClassifier.set("all")
    duplicatesStrategy = DuplicatesStrategy.EXCLUDE

    from(sourceSets.main.get().output)

    dependsOn(configurations.runtimeClasspath)

    from({
        configurations.runtimeClasspath.get()
            .filter { it.name.endsWith("jar") }
            .map { zipTree(it) }
    }) {
        exclude("META-INF/*.SF", "META-INF/*.DSA", "META-INF/*.RSA")
    }

    manifest {
        attributes["Main-Class"] = "${mainClassName}Kt"
    }
}

tasks.build {
    dependsOn(tasks.named("fatJar"))
}

