plugins {
    id("common")
    application
}

repositories {
    maven("https://github-package-registry-mirror.gc.nav.no/cached/maven-release")
}

dependencies {
    implementation(libs.rapids.and.rivers)
    implementation(libs.konfig)
    implementation(libs.kotlin.logging)
    implementation("no.nav.common:audit-log:3.2023.09.13_04.55-a8ff452fbd94")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")

    testImplementation("no.nav.dagpenger:aktivitetslogg:20230914.cfb96c")
    testImplementation("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.get()}")
    testImplementation(libs.mockk)
}

application {
    mainClass.set("no.nav.dagpenger.audit.logger.AppKt")
}
