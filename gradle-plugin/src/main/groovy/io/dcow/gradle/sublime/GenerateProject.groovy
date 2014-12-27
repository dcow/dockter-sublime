package io.dcow.gradle.sublime

import org.gradle.api.DefaultTask
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

import static groovy.json.JsonOutput.toJson
import static groovy.json.JsonOutput.prettyPrint

/**
 * Task for generating a Sublime Text 3 project file from a gradle build.
 * <p>
 * This task takes advantage of project-sepecific build systems in Sublime Text 3.
 * Defining a custom build system means we can insert all the tasks declared by the
 * build.gradle script as
 * <a href="http://docs.sublimetext.info/en/latest/reference/build_systems.html#variants">
 * build system <i>variants</i></a> in the project file (which are made available to the
 * developer as <a href="http://www.sublimetext.com/docs/commands"> sublime text
 * <i>commands</i></a>).
 * The downside of this approach is that the project file must be re-generated every time
 * the task graph changes. (A <a href="dcow.github.io/dockter-sublime/">sublime text gradle
 * plugin</a> is in the works.)
 */
public class GenerateProject extends DefaultTask {

    public static final String DESCRIPTION = 'Generates a Sublime Text 3 project file.'

    @OutputFile
    File projectFile

    // Methods of invoking Gradle
    static final String GRADLE = 'gradle'
    static final String WRAPPER = 'w'

    public GenerateProject() {
        super()

        this.description = DESCRIPTION
        // This task is never up to date as it changes with the build script.
        this.outputs.upToDateWhen {false}
    }

    @TaskAction
    void writeProjectFile() {
        def root = [:]

        ['Folders', 'Settings', 'BuildSystems'].each {
            "add$it" root
        }

        projectFile.write prettyPrint(toJson(root))
    }

    def addFolders(root) {
        // We'll treat Sublime Text folders as Gradle projects
        def rootProject = project.rootProject
        def rootDir = rootProject.projectDir

        root.folders = [
            [
                'name': rootProject.name,
                'path': rootProject.relativePath(rootDir),
                'folder_exclude_patterns': [
                    rootProject.relativePath(rootProject.buildDir),
                    rootProject.relativePath("${rootDir}/.gradle")
                ] + rootProject.subprojects.collect {
                    rootProject.relativePath("${it.projectDir}")
                } // We already iterate over the subprojects set.
            ]    //  Extract to one loop (return list of tuples).
        ] + rootProject.subprojects.collect {
            [
                'name': it.name,
                'path': rootProject.relativePath(it.projectDir),
                'folder_exclude_patterns': [
                    rootProject.relativePath(it.buildDir),
                ]
            ]
        }
    }

    def addSettings(root) {
        root.settings = project.sublime.settings
    }

    def addBuildSystems(root) {
        def gradle = project.sublime.wrapper ? "./$GRADLE$WRAPPER" : GRADLE

        root.build_systems = [
            [
                'name': "Gradle: ${project.sublime.name}",
                'working_dir': '$project_path',
                'cmd': ["$gradle build"],
                'shell': true,
                'file_regex': '^(...*?):([0-9]*):?([0-9]*)',
                'variants': listVariants(gradle)
            ]
        ]
    }

    def listVariants(gradle) {
        def allTasks = project.allprojects.collect{it.tasks}.flatten()
        // for now we will only expose tasks that have a description
        allTasks.findAll{it.description?.trim()}.collect {
            // chop off the leading colon..
            [
                'name': "${it.path.substring(1)} - ${it.description}",
                'cmd': ["$gradle ${it.path}"]
            ]
        }
        // Ideally, we need a better reduction of tasks that includes the tasks that
        // are available by letting top level tasks trickle down. Something like the
        // set of the union of all tasks by task name (not path).
    }

    File getProjectFile() {
        return this.projectFile
    }

    void setProjectFile(File file) {
        this.projectFile = file
    }
}