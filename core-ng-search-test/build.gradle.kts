plugins {
    project
    lint
    lib
    maven
}

dependencies {
    implementation(project(":core-ng-test"))
    implementation(project(":core-ng-search"))
    implementation(libs.elasticsearch) {
        exclude(group = "io.opentelemetry")
    }
    implementation(libs.elasticsearch.plugin.transport.netty4)
    implementation(libs.elasticsearch.plugin.mapper.extras)       // used by elasticsearch scaled_float
    implementation(libs.elasticsearch.plugin.lang.painless)
    implementation(libs.elasticsearch.plugin.analysis.common)     // used by elasticsearch stemmer
    implementation(libs.elasticsearch.plugin.reindex)             // used by elasticsearch deleteByQuery
    runtimeOnly("org.apache.logging.log4j:log4j-to-slf4j:2.19.0") // elasticsearch uses log4j-api:2.19.0
}
