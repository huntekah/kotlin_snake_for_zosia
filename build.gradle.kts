plugins {
    kotlin("jvm") version "1.3.71"
    application
    id("org.openjfx.javafxplugin") version "0.0.8"
}

group = "org.example"
version = "1.0-SNAPSHOT"

repositories {
    jcenter()
    mavenCentral()
    flatDir {
        //  https://sourceforge.net/projects/jfuzzylogic/files/jfuzzylogic/jFuzzyLogic.jar/download
        dirs("libs")
    }
}

val tornadofx_version = "1.7.20"

dependencies {
    implementation(kotlin("stdlib-jdk8"))
    implementation("no.tornado:tornadofx:$tornadofx_version")
    implementation("net.sourceforge.jFuzzyLogic:jFuzzyLogic")
}

javafx {
    version = "11"
    modules("javafx.controls", "javafx.fxml")
}

tasks {
    compileKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
    compileTestKotlin {
        kotlinOptions.jvmTarget = "1.8"
    }
}

application {
    mainClassName = "application.SnakeApp"
}