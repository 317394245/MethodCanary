package cn.hikyson.methodcanary.plugin

import org.apache.commons.io.FileUtils
import org.gradle.api.Project

import java.io.File
import javax.script.Invocable
import javax.script.ScriptEngine
import javax.script.ScriptEngineManager

class IncludesEngine(val project: Project, val androidGodEyeExtension: AndroidGodEyeExtension ): IInExcludes {
    var  mJsEngine: ScriptEngine? = null
    var  mInternalExcludes: InternalExcludes
    var  mExternalIncludes:ExternalIncludes

    init {
        if (androidGodEyeExtension.instrumentationRuleFilePath != null && "" != androidGodEyeExtension.instrumentationRuleFilePath) {
            var methodCanaryJsFile:File = File(project.getRootDir(), androidGodEyeExtension.instrumentationRuleFilePath)
            if (!methodCanaryJsFile.exists() || !methodCanaryJsFile.isFile()) {
                Util.throwException("[AndroidGodEye][MethodCanary] Can not find instrumentationRuleFilePath: " + androidGodEyeExtension.instrumentationRuleFilePath)
            }
            var inExcludeEngineContent = FileUtils.readFileToString(methodCanaryJsFile)
            project.logger.quiet("[AndroidGodEye][MethodCanary] InstrumentationRuleFile:\n" + inExcludeEngineContent)
            this.mJsEngine = ScriptEngineManager().getEngineByName("javascript");
            this.mJsEngine?.eval(inExcludeEngineContent)
        }
        this.mExternalIncludes = ExternalIncludes(androidGodEyeExtension.instrumentationRuleIncludeClassNamePrefix)
        this.mInternalExcludes = InternalExcludes()
    }

    override fun isMethodInclude(classInfo: ClassInfo, methodInfo: MethodInfo): Boolean {
        if (this.mInternalExcludes != null && this.mInternalExcludes.isMethodExclude(classInfo, methodInfo)) {
            //内部需要exclude
            return false
        }
        if (mJsEngine == null) {
            return mExternalIncludes.isMethodInclude(classInfo, methodInfo)
        }
        var isInclude = (mJsEngine as Invocable).invokeFunction("isInclude", classInfo, methodInfo) as Boolean
        return isInclude && mExternalIncludes.isMethodInclude(classInfo, methodInfo)
    }
}