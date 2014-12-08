package me.dcow

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
 * the task graph changes. (A <a href="dcow.github.io/docter-sublime/">sublime text gradle
 * plugin</a> is in the works.)
 */
public class SublimeProject extends DefaultTask {

    public static final String DESCRIPTION = 'Generates a Sublime Text 3 project file.'
    public static final String FILE_EXTENSION = "sublime-project"

    @OutputFile
    File projectFile

    boolean wrapper

    // Methods of invoking Gradle
    static final String GRADLE = 'gradle'
    static final String WRAPPER = 'w'

    public SublimeProject() { 
        super()

        def project = getProject()
        setProperty('description', DESCRIPTION)
        wrapper = true
        projectFile = project.file "${project.name}.$FILE_EXTENSION"
    }

    @TaskAction
    void writeProjectFile() {
        def root = [:]

        root.folders = listFolders()
        root.settings = listSettings()
        root.build_systems = listBuildSystems()
        
        projectFile.write prettyPrint(toJson(root))
    }

    def listFolders() {
        [['path': project.projectDir.getAbsolutePath()]]
    }

    def listSettings() {
        [] // no settings yet
    }

    def listBuildSystems() {
        def gradle = wrapper ? "./$GRADLE$WRAPPER" : GRADLE
        def project = getProject()
        
        def build = [:]
        
        build.name = 'Gradle' 
        build.working_dir = '$project_path'
        build.cmd = [ "$gradle ${project.tasks.build ? 'build' : 'tasks'}"]
        build.shell = true
        build.file_regex = '^(...*?):([0-9]*):?([0-9]*)'
        build.variants = listVariants(gradle)

        [build] // it's a json array
    }

    def listVariants(gradle) {
        // for now we will only expose tasks that have a description
        project.tasks.findAll{it.description?.trim()}.collect {
            // chop off the leading colon..
            def name = it.path.substring(1).capitalize()
            ['name': "$name - ${it.description}", 'cmd': ["$gradle ${it.path}"]]
        }
    }

    boolean getWrapper() {
        return this.wrapper
    }

    void setWrapper(boolean val) {
        this.wrapper = val
    }

    File getProjectFile() {
        return this.projectFile
    }

    void setProjectFile(File file) {
        this.projectFile = file
    }
}