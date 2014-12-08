package me.dcow

import org.gradle.api.Plugin
import org.gradle.api.Project

class Sublime implements Plugin<Project> {
    // Provided for name access to the task..
    public static final String SUBLIME_TASK = 'sublime'

    void apply(Project project) {
        project.tasks.create(name: SUBLIME_TASK, type: SublimeProject)
    }
}