val paperApiVersion: String by project
val paperRecommendedMcVersion: String by project

plugins {
    id("com.gradleup.shadow")
    id("xyz.jpenilla.run-paper")
}

dependencies {
    implementation(project(":core"))
    implementation(project(":nms:paper-1_21_11"))
    implementation(project(":nms:paper-26_1_2"))
    implementation(project(":platform:paper"))

    compileOnly("io.papermc.paper:paper-api:$paperApiVersion")
}

tasks {
    runServer {
        minecraftVersion(paperRecommendedMcVersion)
    }

    shadowJar {
        archiveClassifier.set("")
        mergeServiceFiles()
    }
}

tasks.assemble {
    dependsOn(tasks.shadowJar)
}

tasks.processResources {
    val props = mapOf("version" to project.version)
    inputs.properties(props)
    filteringCharset = "UTF-8"
    filesMatching("plugin.yml") {
        expand(props)
    }
}