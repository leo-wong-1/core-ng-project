plugins {
    project
    lint
    lib
    maven
}

dependencies {
    api(project(":core-ng"))
    api("org.mongodb:mongodb-driver-sync:5.5.1")
    testImplementation(project(":core-ng-test"))
}
