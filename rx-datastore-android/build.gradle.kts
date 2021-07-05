buildscript {
    repositories {
        mavenCentral()
    }
}

plugins {
    id("com.android.library")
    kotlin("android")
    id("com.vanniktech.maven.publish.base")
    jacoco
}

android {
    compileSdkVersion(29)
    defaultConfig {
        minSdkVersion(16)
        targetSdkVersion(29)
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    flavorDimensions("default")

    lintOptions {
        isAbortOnError = false
        isCheckReleaseBuilds = false
    }

    useLibrary("android.test.runner")
    useLibrary("android.test.base")
    useLibrary("android.test.mock")

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    kotlinOptions {
        jvmTarget = "1.8"
        useIR = true
    }
}

dependencies {
    implementation(kotlin("stdlib", version = "1.4.30"))
    implementation("io.reactivex.rxjava2:rxjava:2.2.19")
    implementation("io.reactivex.rxjava2:rxandroid:2.1.1")
    implementation("androidx.preference:preference:1.1.1")
    implementation("com.squareup.moshi:moshi:1.11.0")
    implementation("com.squareup.moshi:moshi-kotlin:1.11.0")

    testImplementation("net.wuerl.kotlin:assertj-core-kotlin:0.1.1")
    testImplementation("junit:junit:4.13.2")
    testImplementation("org.mockito:mockito-core:2.23.4")
    testImplementation("io.mockk:mockk:1.11.0")

    androidTestImplementation("com.squareup.assertj:assertj-android:1.1.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.3.0")
    androidTestImplementation("androidx.test:runner:1.3.0")
    androidTestImplementation("androidx.test:rules:1.3.0")
    androidTestImplementation("androidx.test.ext:junit:1.1.2")
}

plugins.withType<com.vanniktech.maven.publish.MavenPublishBasePlugin>() {
    group = "io.github.yuriykulikov"
    version = "1.0.0"
    mavenPublishing {
        publishToMavenCentral(com.vanniktech.maven.publish.SonatypeHost.S01)

        configure(
            com.vanniktech.maven.publish.AndroidLibrary(
                javadocJar = com.vanniktech.maven.publish.JavadocJar.Javadoc()
            )
        )

        // Will only apply to non snapshot builds.
        signAllPublications()

        pom {
            name.value("rx-datastore-android")
            description.value("DataStore with RxJava2 interfaces")
            inceptionYear.value("2019")
            url.value("https://github.com/yuriykulikov/rx-datastore")
            licenses {
                license {
                    name.value("MIT License")
                    url.value("https://raw.githubusercontent.com/yuriykulikov/rx-datastore/main/LICENSE")
                    distribution.value("https://raw.githubusercontent.com/yuriykulikov/rx-datastore/main/LICENSE")
                }
            }
            developers {
                developer {
                    id.value("yuriykulikov")
                    name.value("Yuriy Kulikov")
                    url.value("https://github.com/yuriykulikov/")
                }
            }
            scm {
                url.value("https://github.com/yuriykulikov/rx-datastore")
                connection.value("scm:git:git://github.com/yuriykulikov/rx-datastore-android.git")
                developerConnection.value("scm:git:ssh://git@github.com/yuriykulikov/rx-datastore-android.git")
            }
        }
    }
}
