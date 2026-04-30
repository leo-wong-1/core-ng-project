plugins {
    project
    lint
    app
}

dependencies {
    implementation(project(":core-ng"))
    testImplementation(project(":core-ng-test"))
}
