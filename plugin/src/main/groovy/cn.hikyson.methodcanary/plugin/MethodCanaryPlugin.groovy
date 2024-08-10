package cn.hikyson.methodcanary.plugin

import com.android.build.api.artifact.ScopedArtifact
import com.android.build.api.variant.ApplicationAndroidComponentsExtension
import com.android.build.api.variant.ScopedArtifacts
import com.android.build.api.variant.ApplicationVariant
import com.android.build.gradle.AppPlugin
import org.gradle.api.Action
import org.gradle.api.GradleException
import org.gradle.api.Plugin
import org.gradle.api.Project

public class MethodCanaryPlugin implements Plugin<Project> {

    @Override
    void apply(Project project) {
        project.logger.quiet("[AndroidGodEye][MethodCanary] Plugin applied.")
        project.extensions.create("AndroidGodEye", AndroidGodEyeExtension.class)

        project.plugins.withType(AppPlugin.class) {
           def androidComponents = project.extensions.getByType(ApplicationAndroidComponentsExtension.class)
            androidComponents.onVariants(androidComponents.selector().all(), new Action<ApplicationVariant>(){
                @Override
                void execute(ApplicationVariant applicationVariant) {
                    def taskProvider = project.tasks.register("${applicationVariant.name}MethodCanaryTask", MethodCanaryTask.class)

                    applicationVariant.artifacts.forScope(ScopedArtifacts.Scope.PROJECT)
                            .use(taskProvider)
                            .toTransform(
                                    ScopedArtifact.CLASSES,
                                    taskProvider.get().allJars,
                                    taskProvider.get().allDirectories,
                                    taskProvider.get().output
                            )
                }
            })
        }
    }
}
