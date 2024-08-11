package cn.hikyson.methodcanary.plugin

import java.util.Arrays

class ClassInfo {
    var access: Int = 0
    var  name:String = ""
    var  superName :String = ""
    var  interfaces: Array<String> = arrayOf()


    override fun toString(): String {
        return "ClassInfo{" +
                "access=" + access +
                ", name='" + name + '\'' +
                ", superName='" + superName + '\'' +
                ", interfaces=" + Arrays.toString(interfaces) +
                '}';
    }
}