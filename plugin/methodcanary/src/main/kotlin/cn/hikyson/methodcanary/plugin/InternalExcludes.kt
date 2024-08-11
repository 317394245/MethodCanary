package cn.hikyson.methodcanary.plugin

class InternalExcludes {

    fun isMethodExclude(classInfo:ClassInfo , methodInfo:MethodInfo ): Boolean {
        return classInfo.name.startsWith("cn/hikyson/methodcanary/lib/")
                || classInfo.name.startsWith("cn/hikyson/godeye/core/")
                || classInfo.name.startsWith("cn/hikyson/godeye/monitor/")
                || classInfo.name.startsWith("cn/hikyson/android/godeye/")
    }
}