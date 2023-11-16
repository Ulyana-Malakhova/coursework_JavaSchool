plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("java")
}

apply(plugin = "io.spring.dependency-management")

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    mavenCentral()
}

java {
    sourceCompatibility = JavaVersion.VERSION_15
    targetCompatibility = JavaVersion.VERSION_15
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("co.elastic.logging:logback-ecs-encoder:1.5.0")
    implementation("org.apache.commons:commons-lang3:3.12.0")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.17.6")

    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.test {
    useJUnitPlatform()
}