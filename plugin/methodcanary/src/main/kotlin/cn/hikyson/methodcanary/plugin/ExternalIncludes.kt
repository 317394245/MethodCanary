package cn.hikyson.methodcanary.plugin

class ExternalIncludes(val includeClassNamePrefix: List<String> ) {

    fun isMethodInclude(classInfo: ClassInfo, methodInfo: MethodInfo?): Boolean {
        if (includeClassNamePrefix == null) {
            return true
        }
        for (prefix in includeClassNamePrefix) {
            if (classInfo.name.startsWith(prefix!!)) {
                return true
            }
        }
        return false
    }
}