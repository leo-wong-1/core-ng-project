plugins {
    project
    lint
    app
}

dependencies {
    implementation(project(":core-ng"))
    implementation(project(":core-ng-mongo"))
    testImplementation(project(":core-ng-test"))
    testImplementation(project(":core-ng-mongo-test"))
}
