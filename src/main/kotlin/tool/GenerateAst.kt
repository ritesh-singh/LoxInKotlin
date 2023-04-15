package tool

import java.io.PrintWriter

fun defineAst(
    outputDir: String,
    baseName: String,
    types: List<String>
) {
    val path = "$outputDir/$baseName.kt"
    val writer = PrintWriter(path, "UTF-8")
    with(writer) {
        println("package lox")
        println()
        println("abstract class $baseName {")

        // AST classes
        types.forEach {
            val className = it.split(":", limit = 2)[0].trim()
            val fields = it.split(":", limit = 2)[1].trim()
            defineType(writer, baseName, className, fields)
        }

        println("}")
        close()
    }
}

fun defineType(writer: PrintWriter, baseName: String, className: String, fields: String) {
    writer.println("\tclass $className($fields): $baseName() {")
    writer.println()
    writer.println("\t}")
}

/**
 * Script for generating AST - generating syntax tree classes
 */
fun main(args: Array<String>) {
//    if (args.size != 1) {
//        System.err.println("Usage: generate_ast <output directory>")
//    }
    val outputDir = "/Users/Z003CF1/learn/compiler/LoxInKotlin/src/main/kotlin/lox/"
    defineAst(
        outputDir = outputDir, "Expr", listOf(
            "Binary   : val left:Expr, val operator:Token, val right:Expr",
            "Grouping : val expression:Expr",
            "Literal  : val value:Any",
            "Unary    : val operator:Token, val right:Expr"
        )
    )
}
