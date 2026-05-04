plugins {
    project
    lint
    lib
}

dependencies {
    api(libs.junit.api)
    api(libs.junit.params)
    api(libs.mockito)
    api(libs.assertj)
    implementation(project(":core-ng"))
    implementation(libs.junit.engine)
    implementation("org.junit.platform:junit-platform-launcher")
    testRuntimeOnly(libs.hsqldb)
}
