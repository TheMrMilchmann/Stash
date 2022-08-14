/*
 * Copyright (c) 2022 Leon Linhart
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in
 * all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */
import com.github.themrmilchmann.build.*
import com.github.themrmilchmann.build.BuildType

plugins {
    `java-library`
    `maven-publish`
    signing
    alias(libs.plugins.extra.java.module.info)
}

group = "io.github.themrmilchmann.stash"
val nextVersion = "0.1.0"
version = when (deployment.type) {
    BuildType.SNAPSHOT -> "$nextVersion-SNAPSHOT"
    else -> nextVersion
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(18))
    }

    withJavadocJar()
    withSourcesJar()
}

tasks {
    compileJava {
        options.javaModuleVersion.set("$version")
        options.release.set(18)
    }

    jar {
        archiveBaseName.set("stash")
    }

    withType<Test> {
        useJUnitPlatform()
    }

    javadoc {
        with(options as StandardJavadocDocletOptions) {
            tags = listOf(
                "apiNote:a:API Note:",
                "implSpec:a:Implementation Requirements:",
                "implNote:a:Implementation Note:"
            )

            addStringOption("-release", "18")
        }
    }
}

publishing {
    repositories {
        maven {
            url = uri(deployment.repo)

            credentials {
                username = deployment.user
                password = deployment.password
            }
        }
    }
    publications {
        create<MavenPublication>("mavenJava") {
            from(components["java"])

            artifactId = "stash"

            pom {
                name.set("Stash")
                description.set("Stash is a Java library that provides capabilities to properly store secrets in memory.")
                packaging = "jar"
                url.set("https://github.com/TheMrMilchmann/Stash")

                licenses {
                    licenses {
                        license {
                            name.set("MIT")
                            url.set("https://github.com/TheMrMilchmann/Stash/blob/master/LICENSE")
                            distribution.set("repo")
                        }
                    }
                }

                developers {
                    developer {
                        id.set("TheMrMilchmann")
                        name.set("Leon Linhart")
                        email.set("themrmilchmann@gmail.com")
                        url.set("https://github.com/TheMrMilchmann")
                    }
                }

                scm {
                    connection.set("scm:git:git://github.com/TheMrMilchmann/Stash.git")
                    developerConnection.set("scm:git:git://github.com/TheMrMilchmann/Stash.git")
                    url.set("https://github.com/TheMrMilchmann/Stash.git")
                }
            }
        }
    }
}

signing {
    isRequired = (deployment.type === BuildType.RELEASE)
    sign(publishing.publications)
}

repositories {
    mavenCentral()
}

extraJavaModuleInfo {
    automaticModule(libs.jsr305.orNull!!.module.toString(), "jsr305")
}

dependencies {
    compileOnlyApi(libs.jsr305)

    implementation(libs.jna) {
        artifact { classifier = "jpms" }
    }
    implementation(libs.jna.platform) {
        artifact { classifier = "jpms" }
    }


    testImplementation(libs.junit.jupiter.api)
    testRuntimeOnly(libs.junit.jupiter.engine)
}