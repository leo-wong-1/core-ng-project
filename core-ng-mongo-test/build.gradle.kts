plugins {
    project
    lint
    lib
    maven
}

dependencies {
    implementation(project(":core-ng-test"))
    implementation(project(":core-ng-mongo"))
    implementation("de.bwaldvogel:mongo-java-server:1.47.0")
}
