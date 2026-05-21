// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    extra.set("room_version", "2.8.4")
}
plugins {
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.compose) apply false
}