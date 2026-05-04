plugins {
    project
    lint
    app
}

dependencies {
    implementation(project(":core-ng"))

    // for parquet
    compileOnly("org.apache.hadoop:hadoop-annotations:3.4.1")
    implementation("org.apache.parquet:parquet-avro:1.17.0")
    implementation("org.apache.avro:avro:1.12.1")
    implementation("org.apache.hadoop:hadoop-common:3.4.1@jar")
    runtimeOnly("commons-collections:commons-collections:3.2.2@jar")
    runtimeOnly("com.fasterxml.woodstox:woodstox-core:5.4.0@jar")
    runtimeOnly("org.codehaus.woodstox:stax2-api:4.2.1@jar")
    runtimeOnly("org.apache.hadoop.thirdparty:hadoop-shaded-guava:1.2.0@jar")

    testImplementation(project(":core-ng-test"))
}
