plugins {
    project
    lint
    lib
    maven
}

dependencies {
    api(project(":core-ng"))
    api(libs.elasticsearch.java) {
        exclude(group = "io.opentelemetry")
        exclude(group = "io.opentelemetry.semconv")
        exclude(group = "com.fasterxml.jackson.core")
    }
    implementation(libs.elasticsearch.rest5.client)
    implementation(libs.jackson.databind)
    testImplementation(project(":core-ng-test"))
}
