@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    // Dev Plugins
    idea
    alias(libs.plugins.spotless)

    // Kotlin
    application
    kotlin("jvm") version libs.versions.kotlin
    alias(libs.plugins.kotlin.serialization)

    // Ktor
    alias(libs.plugins.ktor)

    // DBMS
    alias(libs.plugins.jooq)
    alias(libs.plugins.flyway)
}

group = "server"
version = "0.0.1"

application {
    mainClass.set("io.ktor.server.netty.EngineMain")
}

repositories {
    mavenCentral()
}

dependencies {
    /* Server */
    implementation(libs.ktor.server.core)
    implementation(libs.ktor.server.netty)

    /* Server Plugins */
    implementation(libs.ktor.server.auth)
    implementation(libs.ktor.server.config.yaml)
    implementation(libs.ktor.server.contentNegotiation)
    implementation(libs.ktor.server.metrics.core)
    implementation(libs.ktor.server.metrics.micrometer)
    implementation(libs.ktor.server.hsts)
    implementation(libs.ktor.server.cors)
    implementation(libs.ktor.server.compression)
    implementation(libs.ktor.server.statusPages)
    implementation(libs.ktor.server.sessions)
    implementation(libs.ktor.server.hostCommon)
    implementation(libs.ktor.server.defaultHeaders)
    implementation(libs.ktor.server.cachingHeaders)

    /* Client */
    implementation(libs.ktor.client.core)
    implementation(libs.ktor.client.apache)

    /* Extra */
    implementation(libs.ktor.serialization)
    implementation(libs.ktoml)

    /* Observability */
    implementation(libs.logback)
    implementation(libs.micrometer.registry.prometheus)

    /* DBMS */
    implementation(libs.jooq)
    implementation(libs.postgresql)

    /* Tests */
    implementation(libs.kotlin.junit)
    implementation(libs.ktor.server.tests)
    testImplementation(libs.mockito.kotlin)
    testImplementation(libs.testcontainers.base)
    testImplementation(libs.testcontainers.postgresql)

    jooqGenerator(libs.postgresql)
}

/* **************** */
/* Dev Environment  */
/* **************** */
idea {
    module {
        isDownloadJavadoc = false
        isDownloadSources = true
    }
}

/* **************** */
/* Tasks Config     */
/* **************** */
tasks {
    compileKotlin {
        kotlinOptions {
            freeCompilerArgs = listOf("-Xjsr305=strict")
            jvmTarget = "17"
        }
    }

    /* **************** */
    /* Kotest           */
    /* **************** */
    test {
        useJUnitPlatform()

        filter {
            isFailOnNoMatchingTests = false
        }
    }
}

/* **************** */
/* Lint             */
/* **************** */
spotless {
    val spotlessFiles = properties["spotlessFiles"]?.toString()?.split(",")

    isEnforceCheck = properties.getOrDefault("spotless.enforce", "false") == "true"

    val ktlintVersion = libs.versions.ktlint.getOrElse("0.48.2")

    kotlin {
        val files = fileTree(project.projectDir) {
            if (spotlessFiles.isNullOrEmpty()) {
                include("**/*.kt")
            } else {
                include(spotlessFiles)
            }

            exclude("src/generated/**")
        }

        target(files)

        ktlint(ktlintVersion)
    }
}

val installGitHook by tasks.registering(Copy::class) {
    delete(layout.projectDirectory.file(".git/hooks/pre-commit"))
    from(layout.projectDirectory.file("scripts/git-hooks/pre-commit"))
    into(layout.projectDirectory.dir(".git/hooks"))
    fileMode = 493 // 755 octal
}

/* **************** */
/* Database         */
/* **************** */
val minimmoJdbcDriver = properties.getOrDefault("bandeiras.jdbc.driver", "org.postgresql.Driver")?.toString()
val minimmoJdbcUrl = properties.getOrDefault("bandeiras.jdbc.url", "jdbc:postgresql://localhost:5432/bandeiras")?.toString()
val minimmoJdbcUser = properties.getOrDefault("bandeiras.jdbc.username", "bandeiras")?.toString()
val minimmoJdbcPassword = properties.getOrDefault("bandeiras.jdbc.password", "bandeiras.123")?.toString()

flyway {
    driver = minimmoJdbcDriver
    url = minimmoJdbcUrl
    user = minimmoJdbcUser
    password = minimmoJdbcPassword
    createSchemas = true
    table = "flyway_schema_history"
}

jooq {
    version.set(libs.versions.jooq.getOrElse("3.17.7"))
    edition.set(nu.studer.gradle.jooq.JooqEdition.OSS)

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(false)

            // create("jooq") {
            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN
                jdbc.apply {
                    driver = minimmoJdbcDriver
                    url = minimmoJdbcUrl
                    user = minimmoJdbcUser
                    password = minimmoJdbcPassword
                }
                generator.apply {
                    name = "org.jooq.codegen.KotlinGenerator"
                    database.apply {
                        name = "org.jooq.meta.postgres.PostgresDatabase"
                        inputSchema = "public"
                        includes = "public.*"
                        excludes =
                            "databasechangelog | databasechangeloglock | flyway_schema_history | devices_enum_old"
                    }
                    generate.apply {
                        isPojos = false
                        isRecords = false
                        isRoutines = false
                        isDeprecated = false
                        isFluentSetters = true
                        isJavaTimeTypes = true
                        isPojosToString = false
                        isImmutablePojos = false
                        isPojosAsKotlinDataClasses = false
                        isIndexes = false
                        isSequences = false
                    }
                    target.apply {
                        packageName = "bandeiras.server.persistence.jooq"
                        directory = "src/generated/jooq"
                    }
                    strategy.name = "org.jooq.codegen.DefaultGeneratorStrategy"
                }
            }
        }
    }
}

val flywayMigrate by tasks.existing

val generateJooq by tasks.existing {
    dependsOn(flywayMigrate)
}
