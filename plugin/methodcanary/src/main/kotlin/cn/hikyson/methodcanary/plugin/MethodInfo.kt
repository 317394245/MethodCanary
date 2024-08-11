package cn.hikyson.methodcanary.plugin

class MethodInfo(val access: Int,  val name:String, val desc: String) {

    override fun toString(): String {
        return "MethodInfo{" +
                "access=" + access +
                ", name='" + name + '\'' +
                ", desc='" + desc + '\'' +
                '}';
    }
}