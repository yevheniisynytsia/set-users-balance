plugins {
    java
    id("org.springframework.boot") version "3.3.1"
    id("io.spring.dependency-management") version "1.1.5"
}

group = "com.solidgate.test.task"
version = "0.0.1"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(17)
    }
}

configurations {
    compileOnly {
        extendsFrom(configurations.annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

val functionalTestImplementation by configurations.creating {
    extendsFrom(configurations.testImplementation.get())
}

val functionalTestRuntimeOnly by configurations.creating {
    extendsFrom(configurations.testRuntimeOnly.get())
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("org.liquibase:liquibase-core")
    implementation("org.springframework.boot:spring-boot-starter-validation")

    compileOnly("org.projectlombok:lombok")
    implementation("org.postgresql:postgresql")
    annotationProcessor("org.projectlombok:lombok")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.testcontainers:postgresql")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    functionalTestImplementation("io.rest-assured:rest-assured:5.4.0")
    functionalTestImplementation("org.springframework.boot:spring-boot-starter-test")
    functionalTestImplementation("org.springframework.boot:spring-boot-testcontainers")
    functionalTestImplementation("org.testcontainers:junit-jupiter")
    functionalTestImplementation("org.testcontainers:postgresql")
}

tasks.test {
    jvmArgs = listOf("-Xmx4g")
}

tasks.withType<Test> {
    useJUnitPlatform()
}

sourceSets {
    create("functionalTest") {
        java.srcDir("src/functional/java")
        resources.srcDir("src/functional/resources")

        compileClasspath += sourceSets.getByName("main").output
        compileClasspath += sourceSets.getByName("test").output
        runtimeClasspath += sourceSets.getByName("main").output
        runtimeClasspath += sourceSets.getByName("test").output
    }
}

tasks.register<Test>("functionalTest") {
    testClassesDirs = sourceSets["functionalTest"].output.classesDirs
    classpath = sourceSets["functionalTest"].runtimeClasspath
    useJUnitPlatform()
}

tasks.named("check") {
    dependsOn("functionalTest")
}
