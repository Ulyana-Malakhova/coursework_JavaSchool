plugins {
    id("org.springframework.boot") version "2.3.4.RELEASE"
    id("org.liquibase.gradle") version "2.0.3"
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
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("ma.glasnost.orika:orika-core:1.5.4")
    implementation("org.postgresql:postgresql:42.2.12")

    liquibaseRuntime("org.liquibase:liquibase-core:3.8.1")
    liquibaseRuntime("org.liquibase:liquibase-groovy-dsl:2.1.1")
    liquibaseRuntime("ch.qos.logback:logback-classic:1.2.6")
    liquibaseRuntime("jakarta.xml.bind:jakarta.xml.bind-api:2.3.2")
    liquibaseRuntime("org.postgresql:postgresql:42.2.12")

    compileOnly("org.projectlombok:lombok:1.18.28")
    annotationProcessor("org.projectlombok:lombok:1.18.28")
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:postgresql:1.17.6")

    compileOnly("org.springframework.boot:spring-boot-configuration-processor")
}

tasks.test {
    useJUnitPlatform()
}

extra.apply {
    set("db_url", "jdbc:postgresql://localhost:5432/postgres")
    set("db_user", "postgres")
    set("db_pass", "30032003")
}

liquibase {
    activities.register("main") {
        val db_url by project.extra.properties
        val db_user by project.extra.properties
        val db_pass by project.extra.properties
        this.arguments = mapOf(
                "logLevel" to "info",
                "changeLogFile" to "src/main/resources/migrations/db.changelog-master.xml",
                "url" to db_url,
                "username" to db_user,
                "password" to db_pass,
                "driver" to "org.postgresql.Driver"
        )
    }
    runList = "main"
}