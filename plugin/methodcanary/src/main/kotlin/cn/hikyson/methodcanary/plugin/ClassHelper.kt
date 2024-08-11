package cn.hikyson.methodcanary.plugin

import org.objectweb.asm.MethodVisitor
import org.objectweb.asm.Opcodes

object ClassHelper {

    var ON_ACTIVITY_CREATE: MethodInfoForLifecycle =
        MethodInfoForLifecycle("onCreate", "(Landroid/os/Bundle;)V")

    var ON_ACTIVITY_START: MethodInfoForLifecycle = MethodInfoForLifecycle("onStart", "()V")

    var ON_ACTIVITY_RESUME: MethodInfoForLifecycle = MethodInfoForLifecycle("onResume", "()V")

    var ON_ACTIVITY_PAUSE: MethodInfoForLifecycle = MethodInfoForLifecycle("onPause", "()V")

    var ON_ACTIVITY_STOP: MethodInfoForLifecycle = MethodInfoForLifecycle("onStop", "()V")

    var ON_ACTIVITY_SAVE_INSTANCE_STATE: MethodInfoForLifecycle =
        MethodInfoForLifecycle("onSaveInstanceState", "(Landroid/os/Bundle;)V")

    var ON_ACTIVITY_DESTORY: MethodInfoForLifecycle = MethodInfoForLifecycle("onDestroy", "()V")

    var ON_FRAGMENT_ATTACH: MethodInfoForLifecycle =
        MethodInfoForLifecycle("onAttach", "(Landroid/app/Activity;)V")

    var ON_FRAGMENT_CREATE: MethodInfoForLifecycle =
        MethodInfoForLifecycle("onCreate", "(Landroid/os/Bundle;)V")

    var ON_FRAGMENT_VIEW_CREATE: MethodInfoForLifecycle = MethodInfoForLifecycle(
        "onCreateView",
        "(Landroid/view/LayoutInflater;Landroid/view/ViewGroup;Landroid/os/Bundle;)Landroid/view/View;"
    )

    var ON_FRAGMENT_START: MethodInfoForLifecycle = MethodInfoForLifecycle("onStart", "()V")

    var ON_FRAGMENT_RESUME: MethodInfoForLifecycle = MethodInfoForLifecycle("onResume", "()V")

    var ON_FRAGMENT_PAUSE: MethodInfoForLifecycle = MethodInfoForLifecycle("onPause", "()V")

    var ON_FRAGMENT_STOP: MethodInfoForLifecycle = MethodInfoForLifecycle("onStop", "()V")

    var ON_FRAGMENT_SAVE_INSTANCE_STATE: MethodInfoForLifecycle =
        MethodInfoForLifecycle("onSaveInstanceState", "(Landroid/os/Bundle;)V")

    var ON_FRAGMENT_VIEW_DESTROY: MethodInfoForLifecycle =
        MethodInfoForLifecycle("onDestroyView", "()V")

    var ON_FRAGMENT_DESTROY: MethodInfoForLifecycle = MethodInfoForLifecycle("onDestory", "()V")

    var ON_FRAGMENT_DETACH: MethodInfoForLifecycle = MethodInfoForLifecycle("onDetach", "()V")

    var LIFECYCLE_EVENTS: MutableSet<MethodInfoForLifecycle> = HashSet()

    init {
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_CREATE);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_START);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_RESUME);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_PAUSE);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_STOP);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_SAVE_INSTANCE_STATE);
        LIFECYCLE_EVENTS.add(ON_ACTIVITY_DESTORY);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_ATTACH);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_CREATE);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_VIEW_CREATE);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_START);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_RESUME);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_PAUSE);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_STOP);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_SAVE_INSTANCE_STATE);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_VIEW_DESTROY);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_DESTROY);
        LIFECYCLE_EVENTS.add(ON_FRAGMENT_DETACH);
    }


    fun injectMethodEnter(
        methodCanaryMethodVisitor: MethodCanaryMethodVisitor,
        mv: MethodVisitor
    ): Int {
        if (isLifecycleMethod(methodCanaryMethodVisitor, mv)) {
            if (methodCanaryMethodVisitor.androidGodEyeExtension.enableLifecycleTracer) {
                mv.visitIntInsn(Opcodes.BIPUSH, methodCanaryMethodVisitor.methodInfo.access)
                mv.visitLdcInsn(methodCanaryMethodVisitor.classInfo.name)
                mv.visitLdcInsn(methodCanaryMethodVisitor.methodInfo.name)
                mv.visitLdcInsn(methodCanaryMethodVisitor.methodInfo.desc)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
                mv.visitInsn(Opcodes.DUP)
                mv.visitInsn(Opcodes.ICONST_0)
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.AASTORE)
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "cn/hikyson/methodcanary/lib/MethodCanaryInject",
                    "onMethodEnter",
                    "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)V",
                    false
                )
                return 1
            } else {
                return -1
            }
        } else {
            if (methodCanaryMethodVisitor.androidGodEyeExtension.enableMethodTracer) {
                mv.visitIntInsn(Opcodes.BIPUSH, methodCanaryMethodVisitor.methodInfo.access)
                mv.visitLdcInsn(methodCanaryMethodVisitor.classInfo.name)
                mv.visitLdcInsn(methodCanaryMethodVisitor.methodInfo.name)
                mv.visitLdcInsn(methodCanaryMethodVisitor.methodInfo.desc)
                mv.visitInsn(Opcodes.ICONST_0)
                mv.visitInsn(Opcodes.ACONST_NULL)
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "cn/hikyson/methodcanary/lib/MethodCanaryInject",
                    "onMethodEnter",
                    "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)V",
                    false
                )
                return 0
            } else {
                return -1
            }
        }
    }

    fun injectMethodExit(
        methodCanaryMethodVisitor: MethodCanaryMethodVisitor,
        mv: MethodVisitor
    ): Int {
        if (isLifecycleMethod(methodCanaryMethodVisitor, mv)) {
            if (methodCanaryMethodVisitor.androidGodEyeExtension.enableLifecycleTracer) {
                mv.visitIntInsn(Opcodes.BIPUSH, methodCanaryMethodVisitor.methodInfo.access)
                mv.visitLdcInsn(methodCanaryMethodVisitor.classInfo.name)
                mv.visitLdcInsn(methodCanaryMethodVisitor.methodInfo.name)
                mv.visitLdcInsn(methodCanaryMethodVisitor.methodInfo.desc)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitInsn(Opcodes.ICONST_1)
                mv.visitTypeInsn(Opcodes.ANEWARRAY, "java/lang/Object")
                mv.visitInsn(Opcodes.DUP)
                mv.visitInsn(Opcodes.ICONST_0)
                mv.visitVarInsn(Opcodes.ALOAD, 0)
                mv.visitInsn(Opcodes.AASTORE)
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "cn/hikyson/methodcanary/lib/MethodCanaryInject",
                    "onMethodExit",
                    "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)V",
                    false
                )
                return 1
            } else {
                return -1
            }
        } else {
            if (methodCanaryMethodVisitor.androidGodEyeExtension.enableMethodTracer) {
                mv.visitIntInsn(Opcodes.BIPUSH, methodCanaryMethodVisitor.methodInfo.access)
                mv.visitLdcInsn(methodCanaryMethodVisitor.classInfo.name)
                mv.visitLdcInsn(methodCanaryMethodVisitor.methodInfo.name)
                mv.visitLdcInsn(methodCanaryMethodVisitor.methodInfo.desc)
                mv.visitInsn(Opcodes.ICONST_0)
                mv.visitInsn(Opcodes.ACONST_NULL)
                mv.visitMethodInsn(
                    Opcodes.INVOKESTATIC,
                    "cn/hikyson/methodcanary/lib/MethodCanaryInject",
                    "onMethodExit",
                    "(ILjava/lang/String;Ljava/lang/String;Ljava/lang/String;I[Ljava/lang/Object;)V",
                    false
                )
                return 0
            } else {
                return -1
            }
        }
    }

    private fun isLifecycleMethod(
        methodCanaryMethodVisitor: MethodCanaryMethodVisitor,
        mv: MethodVisitor
    ): Boolean {
        if (isSuperClassObject(methodCanaryMethodVisitor.classInfo)) {
            return false
        }
        if ((methodCanaryMethodVisitor.methodInfo.access and Opcodes.ACC_STATIC) === Opcodes.ACC_STATIC) {
            return false
        }
        if ((methodCanaryMethodVisitor.methodInfo.access and Opcodes.ACC_PRIVATE) === Opcodes.ACC_PRIVATE) {
            return false
        }
        return LIFECYCLE_EVENTS.contains(
            MethodInfoForLifecycle(
                methodCanaryMethodVisitor.methodInfo
            )
        )
    }

    private fun isSuperClassObject(mClassInfo: ClassInfo): Boolean {
        return "java/lang/Object" == mClassInfo.name || "java/lang/Object" == mClassInfo.superName
    }

    class MethodInfoForLifecycle {
        var name: String?
        var desc: String?

        internal constructor(methodInfo: MethodInfo) {
            this.name = methodInfo.name
            this.desc = methodInfo.desc
        }

        internal constructor(name: String?, desc: String?) {
            this.name = name
            this.desc = desc
        }

        override fun equals(o: Any?): Boolean {
            if (this === o) return true
            if (o == null || javaClass != o.javaClass) return false

            val that = o as MethodInfoForLifecycle

            if (if (name != null) name != that.name else that.name != null) return false
            return if (desc != null) desc == that.desc else that.desc == null
        }

        override fun hashCode(): Int {
            var result = if (name != null) name.hashCode() else 0
            result = 31 * result + (if (desc != null) desc.hashCode() else 0)
            return result
        }

        override fun toString(): String {
            return "MethodInfoForLifecycle{" +
                    "name='" + name + '\'' +
                    ", desc='" + desc + '\'' +
                    '}'
        }
    }

}