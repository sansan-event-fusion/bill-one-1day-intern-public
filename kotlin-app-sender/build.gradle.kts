import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

group = "com.sansan.billone"
version = "0.0.1-SNAPSHOT"

val kotlinVersion = "1.7.0"
val ktorVersion = "1.5.3"
val jacksonVersion = "2.12.2"
val logbackVersion = "1.2.3"
val logbackContribVersion = "0.1.5"
val postgresqlVersion = "42.2.19"
val hikariCPVersion = "3.4.5"
val jdbiVersion = "3.18.0"
val gcpPostgresVersion = "1.2.1"
val gcpStorageVersion = "1.113.14"
val gcpTasksVersion = "1.32.1"
val csvVersion = "1.8"
val unicodeVersion = "67.1"
val fuelVersion = "2.3.1"
val mailVersion = "1.6.2"
val freemarkerVersion = "2.3.31"
val pdfVersion = "2.0.22"
val imageioVersion = "1.4.0"
val pdfImageioVersion = "3.0.3"

// test
val assertjVersion = "3.19.0"
val dbSetupVersion = "2.1.0"
val mockkVersion = "1.11.0"
val jsonFuzzyMatchVersion = "0.4.1"

plugins {
    java
    application
    id("org.jetbrains.kotlin.jvm") version "1.7.0"
    id("com.github.johnrengelman.shadow") version "5.2.0"
    id("org.flywaydb.flyway") version "7.6.0"
}

repositories {
    mavenCentral()
    maven { setUrl("https://jitpack.io") }
}

dependencies {
    // Kotlin
    implementation("org.jetbrains.kotlin:kotlin-stdlib-jdk8:$kotlinVersion")
    implementation("org.jetbrains.kotlin:kotlin-reflect:$kotlinVersion")
    // Ktor
    implementation("io.ktor:ktor-server-netty:$ktorVersion")
    implementation("io.ktor:ktor-locations:$ktorVersion")
    implementation("io.ktor:ktor-jackson:$ktorVersion")
    // Jackson（オブジェクト↔︎JSONの変換）
    implementation("com.fasterxml.jackson.core:jackson-databind:$jacksonVersion")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin:$jacksonVersion")
    implementation("com.fasterxml.jackson.datatype:jackson-datatype-jsr310:$jacksonVersion")
    // Log
    implementation("ch.qos.logback:logback-classic:$logbackVersion")
    implementation("ch.qos.logback.contrib:logback-json-classic:$logbackContribVersion")
    implementation("ch.qos.logback.contrib:logback-jackson:$logbackContribVersion")
    // Database
    implementation("org.postgresql:postgresql:$postgresqlVersion")
    implementation("com.zaxxer:HikariCP:$hikariCPVersion")
    implementation(platform("org.jdbi:jdbi3-bom:$jdbiVersion"))
    implementation("org.jdbi:jdbi3-core")
    implementation("org.jdbi:jdbi3-kotlin")
    implementation("org.jdbi:jdbi3-postgres")
    implementation("org.jdbi:jdbi3-jackson2")
    // GCP
    implementation("com.google.cloud.sql:postgres-socket-factory:$gcpPostgresVersion")
    implementation("com.google.cloud:google-cloud-storage:$gcpStorageVersion")
    implementation("com.google.cloud:google-cloud-tasks:$gcpTasksVersion")
    // Unicode
    implementation("com.ibm.icu:icu4j:$unicodeVersion")
    // PDF Optional Components : https://pdfbox.apache.org/2.0/dependencies.html#optional-components
    implementation("com.github.jai-imageio:jai-imageio-core:$imageioVersion")
    implementation("com.github.jai-imageio:jai-imageio-jpeg2000:$imageioVersion")
    implementation("org.apache.pdfbox:jbig2-imageio:$pdfImageioVersion")

    // Test
    testImplementation("io.ktor:ktor-server-test-host:$ktorVersion")
    testImplementation("org.assertj:assertj-core:$assertjVersion")
    testImplementation("com.ninja-squad:DbSetup-kotlin:$dbSetupVersion")
    testImplementation("io.mockk:mockk:$mockkVersion")
    testImplementation("com.github.orangain:json-fuzzy-match:$jsonFuzzyMatchVersion")

}

java {
    sourceCompatibility = JavaVersion.VERSION_11
}

application {
    mainClassName = "io.ktor.server.netty.EngineMain"
}

tasks.assemble {
    dependsOn(tasks.shadowJar)  // assemble実行時にshadowJarを実行する
}

tasks.withType<ShadowJar> {
    archiveClassifier.set("") // shadowJarでjarと同じ名前（でも依存ライブラリを含む）のjarファイルを作る

    // cloud taskをGAE上で実行した場合に、gRPCエラーが出るため以下の対処を実施
    // https://github.com/grpc/grpc-java/issues/5493 
    mergeServiceFiles()
}

tasks.withType<KotlinCompile> {
    kotlinOptions {
        freeCompilerArgs = listOf("-Xjsr305=strict", "-opt-in=kotlin.RequiresOptIn")
        jvmTarget = "11"
    }
}

tasks.compileKotlin {
    kotlinOptions {
        allWarningsAsErrors = true // アプリケーションコードは警告をエラー扱いにする
    }
}

tasks.test {
    val maxParallelTests: String by project
    maxParallelForks = Integer.parseInt(maxParallelTests)
    testLogging {
        exceptionFormat = TestExceptionFormat.FULL
    }
}
