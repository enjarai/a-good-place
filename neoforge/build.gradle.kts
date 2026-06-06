plugins {
    id("com.possible-triangle.neoforge")
}

neoforge {
    dependOn(project(":common"))
    accessWidener(project(":common"))
}

dependencies {
    modCompileOnly("curse.maven:sodium-394468:8111042")

}