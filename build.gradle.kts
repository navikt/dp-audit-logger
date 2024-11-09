import com.github.jengelman.gradle.plugins.shadow.tasks.ShadowJar

plugins {
    id("common")
    alias(libs.plugins.shadow.jar)
    application
}

repositories {
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
}

dependencies {
    implementation(libs.rapids.and.rivers)
    implementation(libs.konfig)
    implementation(libs.kotlin.logging)
    implementation("no.nav.common:audit-log:3.2024.01.24_10.14-f70bae69bd65")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")

    testImplementation(libs.rapids.and.rivers.test)
    testImplementation(libs.dp.aktivitetslogg)
    testImplementation("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.get()}")
    testImplementation(libs.mockk)
}

application {
    mainClass.set("no.nav.dagpenger.audit.logger.AppKt")
}

tasks.withType<ShadowJar> {
    mergeServiceFiles()
}
