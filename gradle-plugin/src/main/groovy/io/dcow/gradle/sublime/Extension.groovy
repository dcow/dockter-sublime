package io.dcow.gradle.sublime

import org.gradle.api.Project

class Extension {
    String name
    boolean wrapper
    final Map settings

    Extension(Project project) {
        name = project.name
        wrapper = true
        settings = [:]
    }

    boolean getWrapper() {
        wrapper
    }

    def setWrapper(boolean use) {
        wrapper = use
    }

    def getSettings() {
        settings
    }
}