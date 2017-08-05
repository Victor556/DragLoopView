
import java.io.File
import java.io.FileFilter
import java.nio.charset.Charset

val f = File(".")
print(f.absolutePath)
f.listFiles().forEach { print(it.extension + "---" + it.name + "\n"); }
println(f.absolutePath)
val s = "D:\\AndroidStudioProjects"
val p = File(s,"ff")
print (". is root:"+f.isRooted)


val list = mutableListOf<String>()
fun isView(f: File) = {
    var bextends = false
    var bhasconstruct = false
    var ret = false
    run out@ {
        f.forEachLine(Charset.defaultCharset()) {
            bextends = bextends || it.contains("extends")
            bhasconstruct = bhasconstruct || it.contains("super(") || it.contains("super(")
            if (bextends && bhasconstruct) {
                ret = true
                return@forEachLine
            }
        }
    }
    ret
}

fun find(dir: File, nameList: MutableList<String> = list): MutableList<String> {
    dir.listFiles { _, name ->
        name.endsWith(".java") || name.endsWith(".kt")
    }.forEach {
        if (it.isFile) find(it, nameList)
        else if ({var b = false; it.forEachLine (){ };b}.invoke()) {

        }
    }
    return nameList
}