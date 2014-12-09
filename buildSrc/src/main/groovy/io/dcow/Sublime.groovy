package io.dcow

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.tasks.Delete

class Sublime implements Plugin<Project> {
    // Provided for name access to the task..
    public static final String TASK_NAME = 'sublime'
    public static final String PROJECT_TASK_NAME = 'sublimeProject'
    public static final String CLEAR_WORKSPACE_TASK_NAME = 'clearWorkspace'

    static final String WORKSPACE_FILE_EXTENSION = 'sublime-workspace'

    void apply(Project project) {
        def tasks = project.tasks
        tasks.create (name: PROJECT_TASK_NAME, type: SublimeProject)
        tasks.create (name: CLEAR_WORKSPACE_TASK_NAME, type: Delete) {
                description "Remove the '$WORKSPACE_FILE_EXTENSION' file."
                delete "${project.name}.$WORKSPACE_FILE_EXTENSION"
                shouldRunAfter tasks.sublimeProject
        }
        tasks.create (name: TASK_NAME) {
                description "Generate a project file and clear the workspace cache."
                dependsOn tasks.sublimeProject, tasks.clearWorkspace
        }
    }
}