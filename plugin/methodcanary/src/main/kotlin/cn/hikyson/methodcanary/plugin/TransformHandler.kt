package cn.hikyson.methodcanary.plugin


import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.File
import java.io.FileOutputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

object TransformHandler {
    /**
     * project根目录下创建js文件：MethodCanary.js
     * @param project
     * @param transformInvocation
     */
    fun handle(project: Project, methodCanaryTask: MethodCanaryTask ) {
//        Collection<TransformInput> inputs = methodCanaryTask.inputs
//        if (inputs == null) {
//            project.logger.quiet("[AndroidGodEye][MethodCanary] TransformHandler handle: inputs == null")
//            return
//        }
        var outputFile = methodCanaryTask.output.get().asFile
//        if (outputFile != null) {
//            outputFile.deleteOnExit()
//            project.logger.quiet("[MethodCanary] TransformHandler handle: outputProvider.deleteAll")
//        }
        FileUtils.forceDeleteOnExit(FileUtil.outputResult(project))
        var result = StringBuilder()
        var androidGodEyeExtension = project.getExtensions().getByType(AndroidGodEyeExtension::class.java)
        project.logger.quiet("[AndroidGodEye][MethodCanary] AndroidGodEyeExtension: " + androidGodEyeExtension)
        var includesEngine = IncludesEngine(project, androidGodEyeExtension)
        project.logger.quiet("[AndroidGodEye][MethodCanary] Inject start.")
//        inputs.each { TransformInput input ->
        methodCanaryTask.inputDirs.get().forEach {  directoryInput: Directory ->
            handleDirectoryInput(project, directoryInput, outputFile, androidGodEyeExtension, includesEngine, result)
        }
        methodCanaryTask.inputJars.get().forEach {  jarInput: RegularFile ->
            handleJarInputs(project, jarInput, outputFile, androidGodEyeExtension, includesEngine, result)
        }
//        }
        project.logger.quiet("[AndroidGodEye][MethodCanary] Inject end.")
        project.logger.quiet("[AndroidGodEye][MethodCanary] Generate result start.")
        FileUtils.writeStringToFile(FileUtil.outputResult(project), result.toString(), "utf-8", false)
        project.logger.quiet(String.format("[AndroidGodEye][MethodCanary] Generate result end: %s", FileUtil.outputResult(project).absolutePath))
    }

    fun handleDirectoryInput(project: Project, directoryInput: Directory, outputFile: File, androidGodEyeExtension: AndroidGodEyeExtension, includesEngine: IncludesEngine, result: StringBuilder ) {
        if (directoryInput.asFile.isDirectory()) {
            directoryInput.asFileTree.forEach {  file:File ->
                if (file.name.endsWith(".class")) {
//                    project.logger.quiet("[MethodCanary] Dealing with class file [" + file.name + "]")
                    var classReader = ClassReader(file.readBytes())
                    var classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    var cv = MethodCanaryClassVisitor(project, classReader, classWriter, androidGodEyeExtension, includesEngine, result)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    var code = classWriter.toByteArray()
                    var fos = FileOutputStream(
                        file.parentFile.absolutePath + File.separator + file.name)
                    fos.write(code)
                    fos.close()
                } else {
//                    project.logger.quiet("[MethodCanary] Exclude file [" + file.name + "]")
                }
            }
        }
        var dest = File(outputFile, directoryInput.asFile.name)
        dest.createNewFile()
        FileUtils.copyDirectory(directoryInput.asFile, dest)
    }

    fun handleJarInputs(project: Project , jarInput: RegularFile , outputFile: File , androidGodEyeExtension: AndroidGodEyeExtension , includesEngine: IncludesEngine , result: StringBuilder ) {
        if (jarInput.asFile.getAbsolutePath().endsWith(".jar")) {
            var jarName = jarInput.asFile.name
            var md5Name = DigestUtils.md5Hex(jarInput.asFile.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length - 4)
            }
            var jarFile = JarFile(jarInput.asFile)
            var enumeration = jarFile.entries()
            var tmpFile = File(jarInput.asFile.getParent() + File.separator + "classes_temp.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            var jarOutputStream = JarOutputStream(FileOutputStream(tmpFile))
            while (enumeration.hasMoreElements()) {
                var jarEntry = enumeration.nextElement() as JarEntry
                var entryName = jarEntry.getName()
                var zipEntry = ZipEntry(entryName)
                var inputStream = jarFile.getInputStream(jarEntry)
                if (entryName.endsWith(".class")) {
//                    project.logger.quiet("[MethodCanary] Dealing with jar [" + jarName + "], class file [" + entryName + "]")
                    jarOutputStream.putNextEntry(zipEntry)
                    var classReader = ClassReader(IOUtils.toByteArray(inputStream))
                    var classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    var cv = MethodCanaryClassVisitor(project, classReader, classWriter, androidGodEyeExtension, includesEngine, result)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    var code = classWriter.toByteArray()
                    jarOutputStream.write(code)
                } else {
//                    project.logger.quiet("[MethodCanary] Exclude jar [" + jarName + "], file [" + entryName + "]")
                    jarOutputStream.putNextEntry(zipEntry)
                    jarOutputStream.write(IOUtils.toByteArray(inputStream))
                }
                jarOutputStream.closeEntry()
            }
            jarOutputStream.close()
            jarFile.close()
            var dest = File(outputFile, jarName + md5Name+".jar")
            dest.createNewFile()
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }
}