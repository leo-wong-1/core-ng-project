plugins {
    `maven-publish`
}

subprojects {
    group = "core.framework"
    version = "9.5.2"
    repositories {
        maven {
            url = uri("https://neowu.github.io/maven-repo/")
            content {
                includeGroup("core.framework.elasticsearch.module")
            }
        }
    }
}

tasks.named<Wrapper>("wrapper") {
    gradleVersion = "latest"
}

val mavenURL = project.properties["mavenURL"]    // usage: "gradlew -PmavenURL=/path clean publish"

subprojects {
    if (mavenURL != null && project.name.startsWith("core-ng")) {
        apply(plugin = "maven-publish")

        val mavenDir = file(mavenURL)
        assert(mavenDir.exists())
        publishing {
            publications {
                create<MavenPublication>("maven") {
                    from(components["java"])
                }
            }
            repositories {
                maven { url = uri(mavenURL) }
            }
        }
    }
}
