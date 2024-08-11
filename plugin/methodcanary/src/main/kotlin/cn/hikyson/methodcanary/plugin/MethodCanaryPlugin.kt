package cn.hikyson.methodcanary.plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.gradle.AppPlugin
import org.gradle.api.Plugin
import org.gradle.api.Project

class MethodCanaryPlugin: Plugin<Project> {
    override fun apply(project: Project) {
        project.logger.quiet("[AndroidGodEye][MethodCanary] Plugin applied.")
        project.extensions.create("AndroidGodEye", AndroidGodEyeExtension::class.java)

        project.plugins.withType(AppPlugin::class.java) {
            var androidComponents = project.extensions.getByType(
                ApplicationAndroidComponentsExtension::class.java)
            androidComponents.onVariants { applicationVariant ->
                var taskProvider = project.tasks.register("${applicationVariant.name}MethodCanaryTask", MethodCanaryTask::class.java)

                    applicationVariant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
                        .use(taskProvider)
                        .toTransform(
                            ScopedArtifact.CLASSES,
                            MethodCanaryTask::inputJars,
                            MethodCanaryTask::inputDirs,
                            MethodCanaryTask::output
                        )
            }

        }
    }
}