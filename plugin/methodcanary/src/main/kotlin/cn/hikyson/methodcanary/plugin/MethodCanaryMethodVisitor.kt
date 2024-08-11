package cn.hikyson.methodcanary.plugin


import org.gradle.api.Project
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes
import org.objectweb.asm.commons.AdviceAdapter

class MethodCanaryMethodVisitor(
    val project: Project,
    val mv: MethodVisitor,
    val classInfo: ClassInfo,
    val methodInfo: MethodInfo,
    val androidGodEyeExtension: AndroidGodEyeExtension,
    val includesEngine: IncludesEngine,
    val result: StringBuilder
) : AdviceAdapter(
    Opcodes.ASM6, mv, methodInfo.access, methodInfo.name, methodInfo.desc
) {

    override  fun  onMethodEnter() {
        if (!this.includesEngine.isMethodInclude(this.classInfo, this.methodInfo)) {
//                this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodEnter [EXCLUDE]: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
            return
        }
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodEnter start: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
        var type = ClassHelper.injectMethodEnter(this, mv)
        if (type >= 0) {
            this.result.append("PU: class [${this.classInfo.name} ], method [${this.methodInfo.toString()}], type [#{MethodEventInjectProtocol.Type.toString(type)}]").append("\n")
        }
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodEnter end: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
    }

    override fun onMethodExit(i: Int) {
        if (!this.includesEngine.isMethodInclude(this.classInfo, this.methodInfo)) {
//                this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit [EXCLUDE]: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
            return
        }
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit start: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
        var type = ClassHelper.injectMethodExit(this, mv)
        if (type >= 0) {
            this.result.append("PO: class [${ this.classInfo.name } ], method [${this.methodInfo}], type [${MethodEventInjectProtocol.Type.toString(type)}]").append("\n")
        }
//            this.mProject.logger.quiet("[MethodCanary] MethodVisitor onMethodExit end: class [" + String.valueOf(this.mClassInfo) + "], method [" + String.valueOf(this.mMethodInfo) + "]")
    }

}