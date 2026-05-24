val paperApiVersion: String by project

dependencies {
    api(project(":platform:api"))

    compileOnly(
        "io.papermc.paper:paper-api:$paperApiVersion"
    )
}