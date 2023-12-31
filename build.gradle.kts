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
    implementation("no.nav.common:audit-log:3.2023.12.12_13.53-510909d4aa1a")
    implementation("com.papertrailapp:logback-syslog4j:1.0.0")

    testImplementation(libs.dp.aktivitetslogg)
    testImplementation("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.get()}")
    testImplementation(libs.mockk)
}

application {
    mainClass.set("no.nav.dagpenger.audit.logger.AppKt")
}
