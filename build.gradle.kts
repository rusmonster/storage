plugins {
    application
    kotlin("jvm") version "2.0.0"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

dependencies {
    testImplementation(kotlin("test"))
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain(17)

    sourceSets.all {
        languageSettings.enableLanguageFeature("ExplicitBackingFields")
    }
}

application {
    mainClass = "org.example.MainKt"
}

tasks.named("run", JavaExec::class) {
    standardInput = System.`in`
}
