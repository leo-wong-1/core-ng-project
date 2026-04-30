plugins {
    project
    lint
    lib
}

val elasticVersion = "9.3.2"
val jacksonVersion = "3.1.0"

dependencies {
    api(project(":core-ng"))
    api("co.elastic.clients:elasticsearch-java:${elasticVersion}") {
        exclude(group = "io.opentelemetry")
        exclude(group = "io.opentelemetry.semconv")
        exclude(group = "com.fasterxml.jackson.core")
    }
    implementation("co.elastic.clients:elasticsearch-rest5-client:${elasticVersion}")
    implementation("tools.jackson.core:jackson-databind:${jacksonVersion}")
    testImplementation(project(":core-ng-test"))
}
