buildscript {
    dependencies {
        classpath("org.flywaydb:flyway-mysql:11.17.0")
    }
}

plugins {
    id("org.jetbrains.kotlin.jvm") version "1.9.25"
    id("org.jetbrains.kotlin.plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.5.7"
    id("io.spring.dependency-management") version "1.1.7"
    id("nu.studer.jooq") version "9.0"
    id("org.flywaydb.flyway") version "11.17.0"
}

group = "com.example"
version = "0.0.1-SNAPSHOT"
description = "auth"

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(21))
    }
}

configurations {
    compileOnly {
        extendsFrom(annotationProcessor.get())
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-security")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework.boot:spring-boot-starter-validation")
    implementation("org.springframework.boot:spring-boot-starter-data-redis")

    implementation("com.mysql:mysql-connector-j")

    implementation("io.jsonwebtoken:jjwt-api:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-impl:0.12.6")
    runtimeOnly("io.jsonwebtoken:jjwt-jackson:0.12.6")

    implementation("org.springdoc:springdoc-openapi-starter-webmvc-ui:2.7.0")

    implementation("com.bucket4j:bucket4j-core:8.7.0")
    implementation("com.bucket4j:bucket4j-redis:8.7.0")

    implementation("org.springframework.boot:spring-boot-starter-jooq")
    jooqGenerator("com.mysql:mysql-connector-j")

    implementation("org.flywaydb:flyway-core")
    implementation("org.flywaydb:flyway-mysql")

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.security:spring-security-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")

    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
}

val dbUrl: String? = project.findProperty("db.url") as String?
val dbUser: String? = project.findProperty("db.username") as String?
val dbPassword: String? = project.findProperty("db.password") as String?
val dbSchema: String? = project.findProperty("db.schema") as String?

flyway {
    url = dbUrl
    user = dbUser
    password = dbPassword
    schemas = arrayOf(dbSchema)
    locations = arrayOf("filesystem:src/main/resources/db/migration")
    baselineOnMigrate = true
}

jooq {
    version.set(dependencyManagement.importedProperties["jooq.version"])

    configurations {
        create("main") {
            generateSchemaSourceOnCompilation.set(true)

            jooqConfiguration.apply {
                logging = org.jooq.meta.jaxb.Logging.WARN

                jdbc = org.jooq.meta.jaxb.Jdbc().apply {
                    driver = "com.mysql.cj.jdbc.Driver"
                    url = dbUrl
                    user = dbUser
                    password = dbPassword
                }

                generator = org.jooq.meta.jaxb.Generator().apply {
                    name = "org.jooq.codegen.KotlinGenerator"

                    database = org.jooq.meta.jaxb.Database().apply {
                        name = "org.jooq.meta.mysql.MySQLDatabase"
                        inputSchema = dbSchema
                        excludes = "flyway_schema_history"
                        forcedTypes.add(
                            org.jooq.meta.jaxb.ForcedType().apply {
                                name = "BOOLEAN"                     // Kotlin/Java Boolean으로 매핑
                                includeTypes = "TINYINT\\(1\\)"      // MySQL 내부 타입
                                includeExpression = ".*\\.EATEN"     // Eaters 테이블의 eaten 컬럼
                            }
                        )
                    }

                    target = org.jooq.meta.jaxb.Target().apply {
                        packageName = "com.example.auth.generated.jooq"
                        directory = "build/generated-src/jooq/main"
                    }

                    generate = org.jooq.meta.jaxb.Generate().apply {
                        isDaos = true
                        isPojos = true
                        isPojosAsKotlinDataClasses = true
                        isFluentSetters = true
                    }
                }
            }
        }
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.named<Test>("test") {
    useJUnitPlatform()
}