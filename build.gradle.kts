import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

plugins {
    kotlin("jvm") version "1.4.0"
    application
}
group = "eu.scisneromam"
version = "1.0"

repositories {
    mavenCentral()
    jcenter()
    maven {
        url = uri("https://dl.bintray.com/kotlin/ktor")
    }
    maven {
        url = uri("https://dl.bintray.com/kotlin/kotlinx")
    }
}

val kotlinVersion: String by project
val ktorVersion: String by project
val logbackVersion: String by project

dependencies {
    testImplementation(kotlin("test-junit5"))
    implementation("org.jetbrains.kotlinx:kotlinx-html-jvm:0.7.2")

    implementation("com.uchuhimo:konf:0.22.1")

    implementation("mysql:mysql-connector-java:8.0.19")
    implementation("org.mariadb.jdbc:mariadb-java-client:2.1.2")
    implementation("com.zaxxer:HikariCP:3.4.2")
    implementation("org.jetbrains.exposed:exposed-java-time:0.26.2")

    implementation("org.jetbrains.exposed:exposed-core:0.26.2")
    implementation("org.jetbrains.exposed:exposed-dao:0.26.2")
    implementation("org.jetbrains.exposed:exposed-jdbc:0.26.2")

    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")

    implementation("ch.qos.logback:logback-classic:$logbackVersion")

    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-server-core:$ktorVersion")
    implementation("io.ktor:ktor-html-builder:$ktorVersion")
    implementation("io.ktor:ktor-server-sessions:$ktorVersion")
    implementation("io.ktor:ktor-server-host-common:$ktorVersion")
    implementation("io.ktor:ktor-gson:$ktorVersion")
    testImplementation("io.ktor:ktor-server-tests:$ktorVersion")

}
tasks.withType<KotlinCompile> {
    kotlinOptions.jvmTarget = "11"
}
application {
    mainClassName = "ServerKt"
}