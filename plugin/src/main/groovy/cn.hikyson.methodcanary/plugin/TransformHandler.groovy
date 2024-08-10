package cn.hikyson.methodcanary.plugin

import org.apache.commons.codec.digest.DigestUtils
import org.apache.commons.io.FileUtils
import org.apache.commons.io.IOUtils
import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.objectweb.asm.ClassReader
import org.objectweb.asm.ClassVisitor
import org.objectweb.asm.ClassWriter

import java.util.jar.JarEntry
import java.util.jar.JarFile
import java.util.jar.JarOutputStream
import java.util.zip.ZipEntry

public class TransformHandler {
    /**
     * project根目录下创建js文件：MethodCanary.js
     * @param project
     * @param transformInvocation
     */
    static void handle(Project project, MethodCanaryTask methodCanaryTask) {
//        Collection<TransformInput> inputs = methodCanaryTask.inputs
//        if (inputs == null) {
//            project.logger.quiet("[AndroidGodEye][MethodCanary] TransformHandler handle: inputs == null")
//            return
//        }
        File outputFile = methodCanaryTask.output.get().asFile
        if (outputFile != null) {
            outputFile.deleteOnExit()
//            project.logger.quiet("[MethodCanary] TransformHandler handle: outputProvider.deleteAll")
        }
        FileUtils.forceDeleteOnExit(FileUtil.outputResult(project))
        StringBuilder result = new StringBuilder()
        AndroidGodEyeExtension androidGodEyeExtension = project.getExtensions().getByType(AndroidGodEyeExtension.class)
        project.logger.quiet("[AndroidGodEye][MethodCanary] AndroidGodEyeExtension: " + androidGodEyeExtension)
        IncludesEngine includesEngine = new IncludesEngine(project, androidGodEyeExtension)
        project.logger.quiet("[AndroidGodEye][MethodCanary] Inject start.")
//        inputs.each { TransformInput input ->
        methodCanaryTask.allDirectories.each { Directory directoryInput ->
                handleDirectoryInput(project, directoryInput, outputFile, androidGodEyeExtension, includesEngine, result)
            }
        methodCanaryTask.allJars.each { RegularFile jarInput ->
                handleJarInputs(project, jarInput, outputFile, androidGodEyeExtension, includesEngine, result)
            }
//        }
        project.logger.quiet("[AndroidGodEye][MethodCanary] Inject end.")
        project.logger.quiet("[AndroidGodEye][MethodCanary] Generate result start.")
        FileUtils.writeStringToFile(FileUtil.outputResult(project), result.toString(), "utf-8", false)
        project.logger.quiet(String.format("[AndroidGodEye][MethodCanary] Generate result end: %s", FileUtil.outputResult(project).absolutePath))
    }

    static void handleDirectoryInput(Project project, Directory directoryInput, File outputFile, AndroidGodEyeExtension androidGodEyeExtension, IncludesEngine includesEngine, StringBuilder result) {
        if (directoryInput.file.isDirectory()) {
            directoryInput.file.eachFileRecurse { File file ->
                if (file.name.endsWith(".class")) {
//                    project.logger.quiet("[MethodCanary] Dealing with class file [" + file.name + "]")
                    ClassReader classReader = new ClassReader(file.bytes)
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new MethodCanaryClassVisitor(project, classReader, classWriter, androidGodEyeExtension, includesEngine, result)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
                    FileOutputStream fos = new FileOutputStream(
                            file.parentFile.absolutePath + File.separator + file.name)
                    fos.write(code)
                    fos.close()
                } else {
//                    project.logger.quiet("[MethodCanary] Exclude file [" + file.name + "]")
                }
            }
        }
        def dest = new File(outputFile, directoryInput.asFile.name)
        FileUtils.copyDirectory(directoryInput.file, dest)
    }

    static void handleJarInputs(Project project, RegularFile jarInput, File outputFile, AndroidGodEyeExtension androidGodEyeExtension, IncludesEngine includesEngine, StringBuilder result) {
        if (jarInput.file.getAbsolutePath().endsWith(".jar")) {
            def jarName = jarInput.name
            def md5Name = DigestUtils.md5Hex(jarInput.file.getAbsolutePath())
            if (jarName.endsWith(".jar")) {
                jarName = jarName.substring(0, jarName.length() - 4)
            }
            JarFile jarFile = new JarFile(jarInput.file)
            Enumeration enumeration = jarFile.entries()
            File tmpFile = new File(jarInput.file.getParent() + File.separator + "classes_temp.jar")
            if (tmpFile.exists()) {
                tmpFile.delete()
            }
            JarOutputStream jarOutputStream = new JarOutputStream(new FileOutputStream(tmpFile))
            while (enumeration.hasMoreElements()) {
                JarEntry jarEntry = (JarEntry) enumeration.nextElement()
                String entryName = jarEntry.getName()
                ZipEntry zipEntry = new ZipEntry(entryName)
                InputStream inputStream = jarFile.getInputStream(jarEntry)
                if (entryName.endsWith(".class")) {
//                    project.logger.quiet("[MethodCanary] Dealing with jar [" + jarName + "], class file [" + entryName + "]")
                    jarOutputStream.putNextEntry(zipEntry)
                    ClassReader classReader = new ClassReader(IOUtils.toByteArray(inputStream))
                    ClassWriter classWriter = new ClassWriter(classReader, ClassWriter.COMPUTE_MAXS)
                    ClassVisitor cv = new MethodCanaryClassVisitor(project, classReader, classWriter, androidGodEyeExtension, includesEngine, result)
                    classReader.accept(cv, ClassReader.EXPAND_FRAMES)
                    byte[] code = classWriter.toByteArray()
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
            def dest = new File(outputFile, jarName + md5Name+".jar")
            FileUtils.copyFile(tmpFile, dest)
            tmpFile.delete()
        }
    }
}