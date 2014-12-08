# Docter Sublime
===

A Gradle plugin for generating Sublime Text 3 project files.

This plugin will generate a Sublime Text 3 project file complete with an inline build system. This means that all your project's tasks will be available via the command palette.

The drawback of this approach is that updating your build system is not fluid. Sublime Text seems to cache your project information (in your workspace file) and does not invalidate this cache when your project is reloaded (odd?). So, ideally we need a Sublime Text plugin that will *dynamically update the editor whenever a build script change is made*. This project aims to fill that gap.
