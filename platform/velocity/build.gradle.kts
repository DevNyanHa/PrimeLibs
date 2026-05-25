val velocityApiVersion: String by project

plugins {
    id("com.gradleup.shadow")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":platform:api"))

    compileOnly(
        "com.velocitypowered:velocity-api:$velocityApiVersion"
    )
}

tasks {
    shadowJar {
        archiveClassifier.set("")
    }
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("velocity-plugin.json") {
        expand(props)
    }
}