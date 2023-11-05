import lox.Lox
import tool.GenerateAst

fun main(args: Array<String>) {
//    Lox.init(args)
    GenerateAst().init(arrayOf("src/main/kotlin/lox"))
}