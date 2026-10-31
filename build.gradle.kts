// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    dependencies {
        // AGP 9 bundles KGP 2.2.10 for built-in Kotlin; this overrides it with the catalog version.
        classpath(libs.kotlin.gradle.plugin)
    }
}

plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.detekt) apply false
    alias(libs.plugins.spotless)
    alias(libs.plugins.dependency.analysis)
    alias(libs.plugins.cpd)
}

// Reports copy-pasted Kotlin. It names the sources itself, since it only finds Java plugin sources.
cpd {
    language = "kotlin"
    toolVersion = libs.versions.pmd.get()
}

tasks.cpdCheck {
    source = fileTree("app/src") { include("**/*.kt") }
    ignoreFailures = true
}

// Formats all Kotlin sources and Gradle scripts with ktlint (rules in .editorconfig).
spotless {
    // LF like .gitattributes, so the check doesn't depend on how Git checked a file out.
    lineEndings = com.diffplug.spotless.LineEnding.UNIX
    val ktlintVersion = libs.versions.ktlint.get()
    // Targets name the source folders directly so Gradle never walks into build/ directories,
    // which other tasks rewrite during the same build.
    kotlin {
        target("*/src/**/*.kt")
        ktlint(ktlintVersion)
    }
    kotlinGradle {
        target("*.gradle.kts", "*/*.gradle.kts")
        ktlint(ktlintVersion)
    }
}
