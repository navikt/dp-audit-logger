plugins {
    id("common")
    application
}

dependencies {
    implementation(libs.rapids.and.rivers)
    implementation(libs.konfig)
    implementation(libs.kotlin.logging)

    testImplementation("io.kotest:kotest-assertions-core-jvm:${libs.versions.kotest.get()}")
    testImplementation(libs.mockk)
}

application {
    mainClass.set("no.nav.dagpenger.audit.logger.AppKt")
}
