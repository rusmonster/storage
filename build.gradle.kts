plugins {
    application
    kotlin("jvm") version "2.0.0"
    id("com.adarshr.test-logger") version "4.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
    testImplementation("io.mockk:mockk:1.13.11")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)
}

application {
    mainClass = "org.example.MainKt"
}

tasks.named("run", JavaExec::class) {
    standardInput = System.`in`
}
