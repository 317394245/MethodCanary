package cn.hikyson.methodcanary.plugin

import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction

abstract class MethodCanaryTask: DefaultTask() {

    @get:InputFiles
    abstract val inputJars: ListProperty<RegularFile>

    @get:InputFiles abstract val inputDirs: ListProperty<Directory>

    @get:OutputDirectory
    abstract val output: RegularFileProperty

    @TaskAction
    fun taskAction()  {
        project.logger.quiet("[AndroidGodEye][MethodCanary] ======================== transform start ========================")
        var startTime = System.currentTimeMillis()
        TransformHandler.handle(project, this)
        var cost = (System.currentTimeMillis() - startTime) / 1000
        project.logger.quiet("[AndroidGodEye][MethodCanary] Submit issue in [https://github.com/Kyson/AndroidGodEye/issues] if you have any question.")
        project.logger.quiet("[AndroidGodEye][MethodCanary] ======================== transform end, cost " + cost + " s ========================")
    }

}