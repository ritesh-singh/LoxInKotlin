package tool

import java.io.PrintWriter
import java.util.*
import kotlin.system.exitProcess

class GenerateAst {
    fun init(args: Array<String>) {
        if (args.size != 1) {
            System.err.println("Usage: generate_ast <output directory>")
            exitProcess(64)
        }
        val outputDir = args[0]
        defineAst(
            outputDir, "Expr", listOf(
                "Binary   : val left: Expr, val operator: Token, val right: Expr",
                "Grouping : val expression: Expr",
                "Literal  : val value: Any",
                "Unary    : val operator: Token, val right: Expr"
            )
        )
    }


    private fun defineAst(
        outputDir: String, baseName: String, types: List<String>
    ) {
        val path = "$outputDir/$baseName.kt"
        val writer = PrintWriter(path, "UTF-8")
        writer.println("package lox")
        writer.println()
        writer.println("abstract class $baseName {")

        writer.println()
        defineVisitor(writer, baseName, types)
        writer.println()

        // The AST classes.
        for (type in types) {
            val className = type.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
            val fields = type.substringAfter(":").trim()
            defineType(writer, baseName, className, fields)
        }

        writer.println()
        writer.println("  abstract fun <R> accept(visitor: Visitor<R>): R")

        writer.println("}")
        writer.close()
    }

    private fun defineVisitor(
        writer: PrintWriter, baseName: String, types: List<String>
    ) {
        writer.println("  interface Visitor<R> {")
        for (type: String in types) {
            val typeName = type.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
            writer.println(
                "    fun visit$typeName$baseName(${baseName.lowercase(Locale.getDefault())}: $typeName): R"
            )
        }
        writer.println("  }")
    }

    private fun defineType(
        writer: PrintWriter, baseName: String,
        className: String, fieldList: String
    ) {
        writer.println(
            "  class $className($fieldList) : $baseName() {"
        )
        writer.println("    override fun <R> accept(visitor: Visitor<R>): R {")
        writer.println("      return visitor.visit$className$baseName(this)")
        writer.println("    }");


        writer.println("  }")
        writer.println()
    }
}
