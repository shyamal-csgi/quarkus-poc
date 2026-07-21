plugins {
    java
    id("io.quarkus")
}

group = "com.quarkus.poc"
version = "1.0.0-SNAPSHOT"

val quarkusPlatformGroupId = project.property("quarkusPlatformGroupId") as String
val quarkusPlatformArtifactId = project.property("quarkusPlatformArtifactId") as String
val quarkusPlatformVersion = project.property("quarkusPlatformVersion") as String

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(25))
    }
}

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    implementation(enforcedPlatform("${quarkusPlatformGroupId}:${quarkusPlatformArtifactId}:${quarkusPlatformVersion}"))

    implementation("io.quarkus:quarkus-rest")
    implementation("io.quarkus:quarkus-rest-jackson")
    implementation("io.quarkus:quarkus-rest-client")
    implementation("io.quarkus:quarkus-rest-client-jackson")
    implementation("io.quarkus:quarkus-smallrye-fault-tolerance")
    implementation("io.quarkus:quarkus-hibernate-orm")
    implementation("io.quarkus:quarkus-jdbc-postgresql")
    implementation("io.quarkus:quarkus-agroal")
    implementation("io.quarkus:quarkus-flyway")
    implementation("io.quarkus:quarkus-messaging-kafka")
    implementation("io.quarkus:quarkus-cache")
    implementation("io.quarkus:quarkus-hibernate-validator")
    implementation("io.quarkus:quarkus-smallrye-health")
    implementation("io.quarkus:quarkus-smallrye-openapi")
    implementation("io.quarkus:quarkus-micrometer-registry-prometheus")
    implementation("io.quarkus:quarkus-config-yaml")

    compileOnly("org.projectlombok:lombok:1.18.38")
    annotationProcessor("org.projectlombok:lombok:1.18.38")

    testImplementation("io.quarkus:quarkus-junit5")
    testImplementation("io.quarkus:quarkus-junit5-mockito")
    testImplementation("io.rest-assured:rest-assured")
    testImplementation("org.assertj:assertj-core:3.27.3")
    testImplementation(platform("org.testcontainers:testcontainers-bom:1.20.6"))
    testImplementation("org.testcontainers:testcontainers")
    testImplementation("org.testcontainers:postgresql")
    testImplementation("org.testcontainers:kafka")
    testImplementation("org.testcontainers:junit-jupiter")
    testImplementation("org.wiremock:wiremock-standalone:3.12.1")
    testImplementation("com.atlassian.oai:swagger-request-validator-restassured:2.44.1")
    testImplementation("org.awaitility:awaitility:4.2.2")
    testImplementation("io.smallrye.reactive:smallrye-reactive-messaging-in-memory")

    testCompileOnly("org.projectlombok:lombok:1.18.38")
    testAnnotationProcessor("org.projectlombok:lombok:1.18.38")
}

tasks.withType<Test>().configureEach {
    systemProperty("java.util.logging.manager", "org.jboss.logmanager.LogManager")
    // Quiet Java 25 / Quarkus test JVM (Migration Guide 3.31+)
    jvmArgs(
        "--enable-native-access=ALL-UNNAMED",
        "-Djdk.attach.allowAttachSelf=true"
    )
    useJUnitPlatform()
}

// Default `test` = unit tier only (fast, no Docker)
tasks.named<Test>("test") {
    description = "Unit tests (unit/**); fast, no Docker"
    filter {
        includeTestsMatching("unit.*")
        excludeTestsMatching("component.*")
        excludeTestsMatching("blackbox.*")
        excludeTestsMatching("integration.*")
    }
}

tasks.register<Test>("componentTest") {
    description = "Component tests (component/**); needs Docker / Testcontainers"
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    filter {
        includeTestsMatching("component.*")
    }
    shouldRunAfter(tasks.named("test"))
}

tasks.register<Test>("blackboxTest") {
    description = "Blackbox tests (blackbox/**); needs Docker"
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    filter {
        includeTestsMatching("blackbox.*")
    }
    shouldRunAfter(tasks.named("componentTest"))
}

tasks.register("ciTest") {
    description = "CI gate: unit + component + blackbox"
    group = "verification"
    dependsOn("test", "componentTest", "blackboxTest")
}

tasks.register<Test>("integrationTest") {
    description = "Integration tests (integration/**); Testcontainers Kafka; excluded from ciTest"
    group = "verification"
    testClassesDirs = sourceSets["test"].output.classesDirs
    classpath = sourceSets["test"].runtimeClasspath
    filter {
        includeTestsMatching("integration.*")
    }
}

fun k6Env(): Map<String, String> {
    val baseUrl = (findProperty("baseUrl") as String?) ?: System.getenv("BASE_URL") ?: "http://localhost:8080"
    val vus = (findProperty("vus") as String?) ?: System.getenv("VUS") ?: "10"
    val duration = (findProperty("duration") as String?) ?: System.getenv("DURATION") ?: "5m"
    return mapOf(
        "BASE_URL" to baseUrl,
        "VUS" to vus,
        "DURATION" to duration
    )
}

tasks.register<Exec>("perfSmoke") {
    description = "k6 smoke (1 VU, short); override with -PbaseUrl=..."
    group = "verification"
    workingDir = projectDir
    environment(k6Env())
    commandLine("k6", "run", "performance-test/k6-smoke-test.js")
}

tasks.register<Exec>("perfLoad") {
    description = "k6 load (default 5m); override with -Pvus=50 -Pduration=5m -PbaseUrl=..."
    group = "verification"
    workingDir = projectDir
    environment(k6Env())
    commandLine("k6", "run", "performance-test/k6-load-test.js")
}
