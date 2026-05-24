val paperApiVersion: String by project

dependencies {
    implementation(project(":core"))

    compileOnly(
        "io.papermc.paper:paper-api:$paperApiVersion"
    )
}