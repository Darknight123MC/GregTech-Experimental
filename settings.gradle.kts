pluginManagement {
    resolutionStrategy {
        eachPlugin {
            if (requested.id.namespace == "net.minecraftforge.gradle") {
                useModule("com.github.Su5eD:ForgeGradle:${requested.version}")
            }
        }
    }

    repositories {
        gradlePluginPortal()
        maven {
            url = uri("https://jitpack.io/")
        }
        maven {
            url = uri("https://maven.minecraftforge.net/")
        }
    }
}

rootProject.name = "GregTech-Experimental"
