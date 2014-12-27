package io.dcow.gradle.sublime

import org.gradle.api.Plugin
import org.gradle.api.Project

import org.gradle.api.tasks.Delete

class Plugin implements Plugin<Project> {
    // Provided for name access to the task..
    public static final String TASK_NAME = 'sublime'
    public static final String PROJECT_TASK_NAME = 'generateProject'
    public static final String CLEAR_WORKSPACE_TASK_NAME = 'clearWorkspace'

    static final String GROUP = TASK_NAME.capitalize()
    static final String FILE_EXTENSION = "sublime-project"
    static final String WORKSPACE_FILE_EXTENSION = 'sublime-workspace'

    void apply(Project project) {
        def sublime = project.extensions.create('sublime', Extension, project)

        def tasks = project.tasks
        def group = []

        group << tasks.create (name: PROJECT_TASK_NAME, type: GenerateProject)
        group << tasks.create (name: CLEAR_WORKSPACE_TASK_NAME, type: Delete) {
            description "Remove the '$WORKSPACE_FILE_EXTENSION' file."
            mustRunAfter tasks.generateProject
        }
        group << tasks.create (name: TASK_NAME) {
            description "Generate a project file and clear the workspace cache."
            dependsOn tasks.generateProject, tasks.clearWorkspace
        }

        group*.configure {
            it.group = GROUP
        }

        project.afterEvaluate {
            // I'm not entirely sure how the @OutputFile contract works, but I don't think it
            // will pick up changes to the file name if we configure it before the extension
            // has been configured by the user.
            tasks."$PROJECT_TASK_NAME" {
                projectFile = project.file("${sublime.name}.$FILE_EXTENSION")
            }

            tasks."$CLEAR_WORKSPACE_TASK_NAME" {
                delete "${sublime.name}.$WORKSPACE_FILE_EXTENSION"
            }
        }
    }
}