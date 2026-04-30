plugins {
    project
    lint
    app
}

dependencies {
    implementation(project(":core-ng"))
    implementation(project(":core-ng-search"))
    testImplementation(project(":core-ng-test"))
    testImplementation(project(":core-ng-search-test"))
}
