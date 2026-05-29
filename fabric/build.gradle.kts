plugins {
    id("com.possible-triangle.fabric")
}

fabric {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

dependencies {

    modCompileOnly("curse.maven:sodium-394468:8111043")

}