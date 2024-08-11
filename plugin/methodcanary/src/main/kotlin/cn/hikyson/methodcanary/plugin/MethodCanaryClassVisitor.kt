package cn.hikyson.methodcanary.plugin


import org.gradle.api.Project
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

class MethodCanaryClassVisitor(project: Project, classReader: ClassReader, cv: ClassVisitor, androidGodEyeExtension: AndroidGodEyeExtension, includesEngine: IncludesEngine, result: StringBuilder ): ClassVisitor(
    Opcodes.ASM9, cv) {

    var  mProject:Project
    var  mIncludesEngine:IncludesEngine
    var  mAndroidGodEyeExtension:AndroidGodEyeExtension
    var  mClassInfo:ClassInfo
    var  mResult:StringBuilder
    var  mClassReader:ClassReader

    init {
        this.mProject = project
        this.mIncludesEngine = includesEngine
        this.mAndroidGodEyeExtension = androidGodEyeExtension
        this.mClassInfo = ClassInfo()
        this.mResult = result
        this.mClassReader = classReader
    }

    override fun visit(version: Int, access: Int,  name:String,  signature:String?,  superName:String, interfaces:Array<String>) {
        super.visit(version, access, name, signature, superName, interfaces)
        this.mClassInfo.access = access
        this.mClassInfo.name = name
        this.mClassInfo.superName = superName
        this.mClassInfo.interfaces = interfaces
//        this.mProject.logger.quiet("[MethodCanary] ClassVisitor visit class " + String.valueOf(this.mClassInfo))
    }

    override  fun visitMethod(access: Int,  name:String,  desc: String, signature:String?, exceptions: Array<String>?): MethodVisitor {
        var methodInfo = MethodInfo(access, name, desc)
//        this.mProject.logger.quiet("[MethodCanary] ClassVisitor visit method " + String.valueOf(methodInfo) + " " + String.valueOf(this.mClassInfo))
        var methodVisitor = cv.visitMethod(access, name, desc, signature, exceptions)
        methodVisitor = MethodCanaryMethodVisitor(this.mProject, methodVisitor, this.mClassInfo, methodInfo, mAndroidGodEyeExtension, this.mIncludesEngine, this.mResult)
        return methodVisitor
    }
}