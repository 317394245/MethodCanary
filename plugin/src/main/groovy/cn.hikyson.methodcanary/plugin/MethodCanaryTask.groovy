package cn.hikyson.methodcanary.plugin

import com.android.build.api.transform.TransformException
import com.android.build.api.transform.TransformInvocation
import org.gradle.api.DefaultTask
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.jetbrains.annotations.NotNull


abstract class MethodCanaryTask extends DefaultTask {

    @InputFiles
    final abstract  ListProperty<RegularFile> allJars

    @InputFiles abstract  ListProperty<Directory> allDirectories

    @OutputFile
    final abstract  RegularFileProperty output

//    @Internal Set<String> jarPaths = new HashSet<String>()

    @TaskAction
    void transform() throws InterruptedException, IOException {
        mProject.logger.quiet("[AndroidGodEye][MethodCanary] ======================== transform start ========================")
        def startTime = System.currentTimeMillis()
        TransformHandler.handle(project, this)
        def cost = (System.currentTimeMillis() - startTime) / 1000
        mProject.logger.quiet("[AndroidGodEye][MethodCanary] Submit issue in [https://github.com/Kyson/AndroidGodEye/issues] if you have any question.")
        mProject.logger.quiet("[AndroidGodEye][MethodCanary] ======================== transform end, cost " + cost + " s ========================")
    }
}