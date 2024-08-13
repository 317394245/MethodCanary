package cn.hikyson.methodcanary.plugin

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.tasks.InputFiles
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.OutputDirectory
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassWriter
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.InputStream
import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

abstract class MethodCanaryTask: DefaultTask() {

    @get:InputFiles
    abstract val inputJars: ListProperty<RegularFile>

    @get:InputFiles abstract val inputDirs: ListProperty<Directory>

    @get:OutputFile
    abstract val output: RegularFileProperty

    @Internal
    val jarPaths = mutableSetOf<String>()

    @TaskAction
    fun taskAction()  {
        project.logger.quiet("[AndroidGodEye][MethodCanary] ======================== transform start ========================")
        var startTime = System.currentTimeMillis()
        handle(project, this)
        var cost = (System.currentTimeMillis() - startTime) / 1000
        project.logger.quiet("[AndroidGodEye][MethodCanary] Submit issue in [https://github.com/Kyson/AndroidGodEye/issues] if you have any question.")
        project.logger.quiet("[AndroidGodEye][MethodCanary] ======================== transform end, cost " + cost + " s ========================")
    }

    fun handle(project: Project, methodCanaryTask: MethodCanaryTask ) {
//        Collection<TransformInput> inputs = methodCanaryTask.inputs
//        if (inputs == null) {
//            project.logger.quiet("[AndroidGodEye][MethodCanary] TransformHandler handle: inputs == null")
//            return
//        }

        val jarOutput = JarOutputStream(BufferedOutputStream(FileOutputStream(
            output.get().asFile
        )))

        FileUtils.forceDeleteOnExit(FileUtil.outputResult(project))
        var result = StringBuilder()
        var androidGodEyeExtension = project.getExtensions().getByType(AndroidGodEyeExtension::class.java)
        project.logger.quiet("[AndroidGodEye][MethodCanary] AndroidGodEyeExtension: " + androidGodEyeExtension)
        var includesEngine = IncludesEngine(project, androidGodEyeExtension)
        project.logger.quiet("[AndroidGodEye][MethodCanary] Inject start.")

        methodCanaryTask.inputDirs.get().forEach {  directoryInput: Directory ->
            handleDirectoryInput(project, directoryInput, jarOutput, androidGodEyeExtension, includesEngine, result)
        }
        methodCanaryTask.inputJars.get().forEach {  jarInput: RegularFile ->
            handleJarInputs(project, jarInput, jarOutput, androidGodEyeExtension, includesEngine, result)
        }
        jarOutput.close()
        project.logger.quiet("[AndroidGodEye][MethodCanary] Inject end.")
        project.logger.quiet("[AndroidGodEye][MethodCanary] Generate result start.")
        FileUtils.writeStringToFile(FileUtil.outputResult(project), result.toString(), "utf-8", false)
        project.logger.quiet(String.format("[AndroidGodEye][MethodCanary] Generate result end: %s", FileUtil.outputResult(project).absolutePath))
    }

    fun handleDirectoryInput(project: Project, directoryInput: Directory, jarOutput: JarOutputStream, androidGodEyeExtension: AndroidGodEyeExtension, includesEngine: IncludesEngine, result: StringBuilder ) {
        if (directoryInput.asFile.isDirectory()) {
            directoryInput.asFileTree.forEach {  file: File ->
                if (file.name.endsWith(".class")) {
                    println("handleDirectoryInput file: ${file.path}")
//                    project.logger.quiet("[MethodCanary] Dealing with class file [" + file.name + "]")
                    var classReader = ClassReader(file.readBytes())
                    var classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    var cv = MethodCanaryClassVisitor(project, classReader, classWriter, androidGodEyeExtension, includesEngine, result)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)

                    val relativePath = directoryInput.asFile.toURI().relativize(file.toURI()).getPath()
                    println("handleDirectoryInput relativePath: $relativePath")
                    jarOutput.writeEntity(relativePath.replace(File.separatorChar, '/'), file.inputStream())
                } else {
//                    project.logger.quiet("[MethodCanary] Exclude file [" + file.name + "]")
                }
            }
        }


    }

    fun handleJarInputs(project: Project, jarInput: RegularFile, jarOutput: JarOutputStream, androidGodEyeExtension: AndroidGodEyeExtension, includesEngine: IncludesEngine, result: StringBuilder ) {
        if (jarInput.asFile.getAbsolutePath().endsWith(".jar")) {
            val jarFile = JarFile(jarInput.asFile)
            jarFile.entries().iterator().forEach { jarEntry ->
                println("Adding from jar ${jarEntry.name}")
                if (jarEntry.name.endsWith(".class")) {
//                    project.logger.quiet("[MethodCanary] Dealing with jar [" + jarName + "], class file [" + entryName + "]")
                    var classReader = ClassReader(jarFile.getInputStream(jarEntry))
                    var classWriter = ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    var cv = MethodCanaryClassVisitor(project, classReader, classWriter, androidGodEyeExtension, includesEngine, result)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    jarOutput.writeEntity(jarEntry.name, classWriter.toByteArray())
                } else {
//                    project.logger.quiet("[MethodCanary] Exclude jar [" + jarName + "], file [" + entryName + "]")
                    jarOutput.writeEntity(jarEntry.name, jarFile.getInputStream(jarEntry))
                }
            }
            jarFile.close()
        }
    }


    // writeEntity methods check if the file has name that already exists in output jar
    private fun JarOutputStream.writeEntity(name: String, inputStream: InputStream) {
        // check for duplication name first
        if (jarPaths.contains(name)) {
            printDuplicatedMessage(name)
        } else {
            putNextEntry(JarEntry(name))
            inputStream.copyTo(this)
            closeEntry()
            jarPaths.add(name)
        }
    }

    private fun JarOutputStream.writeEntity(relativePath: String, byteArray: ByteArray) {
        // check for duplication name first
        if (jarPaths.contains(relativePath)) {
            printDuplicatedMessage(relativePath)
        } else {
            putNextEntry(JarEntry(relativePath))
            write(byteArray)
            closeEntry()
            jarPaths.add(relativePath)
        }
    }

    private fun printDuplicatedMessage(name: String) =
        println("Cannot add ${name}, because output Jar already has file with the same name.")
}