# DocterSublime

Tools for developing Gradle projects using Sublime Text 3.

### Gradle Plugin

Applying the `sublime` plugin adds a task named `sublime` to your Gradle project. Executing the task causes a *<b>Name</b>.sublime-project* file to be generated with configuration inforamtion relevant to Sublime Text. The project description is complete with an inline buildsystem. This means all your project's tasks will be available via the command palette.

The drawback of this approach is that updating your project is not fluid. Sublime Text stores your latest configuration in a *<b>Name</b>.sublime-workspace* filee. Sublime also [seems to have issues reloading project information][1]. So, ideally we need a Sublime Text plugin that will *dynamically update the editor whenever a build script change is made*.

A Sublime Text plugin does not obselete a Gradle plugin. The plugin also configures Sublime with the names and paths of your Gradle subprojects and excludes your projects build directories. It allows you to configure arbitrary project specific settings (in fact all objects). 

##### Configuration

    sublime {
        wrapper false // default: true
        projectFile file('Other.sublime-project') // default: "$name.sublime-project"
    }

[1]: https://www.sublimetext.com/forum/viewtopic.php?f=2&t=5342#p37042 

### Sublime Text Plugin

 The Sublime Text 3 plugin's ultimate goal is to be a Gradle project IDE. That will take some time. Initially I would like to remove the need to generate a static build system description by dynamically adding commands to Sublime.

 Then lint, code completion, and *maybe* debugging.

