plugins {
    project
    lint
    lib
}

val elasticVersion = "9.3.2"

dependencies {
    implementation(project(":core-ng-test"))
    implementation(project(":core-ng-search"))
    implementation("org.elasticsearch:elasticsearch:${elasticVersion}") {
        exclude(group = "io.opentelemetry")
    }
    implementation("org.elasticsearch.plugin:transport-netty4:${elasticVersion}")
    implementation("core.framework.elasticsearch.module:mapper-extras:${elasticVersion}")       // used by elasticsearch scaled_float
    implementation("core.framework.elasticsearch.module:lang-painless:${elasticVersion}")
    implementation("core.framework.elasticsearch.module:analysis-common:${elasticVersion}")     // used by elasticsearch stemmer
    implementation("core.framework.elasticsearch.module:reindex:${elasticVersion}")             // used by elasticsearch deleteByQuery
    runtimeOnly("org.apache.logging.log4j:log4j-to-slf4j:2.19.0")
}
