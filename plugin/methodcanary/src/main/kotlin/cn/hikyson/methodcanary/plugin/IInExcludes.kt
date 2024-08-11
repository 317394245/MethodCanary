package cn.hikyson.methodcanary.plugin

interface IInExcludes {
    fun isMethodInclude(classInfo:ClassInfo , methodInfo: MethodInfo): Boolean
}