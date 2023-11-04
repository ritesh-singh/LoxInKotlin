package tool

import java.io.PrintWriter
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
                "Binary   : Expr left, Token operator, Expr right",
                "Grouping : Expr expression",
                "Literal  : Object value",
                "Unary    : Token operator, Expr right"
            )
        )
    }


    private fun defineAst(
        outputDir: String, baseName: String, types: List<String>
    ) {
        val path = "$outputDir/$baseName.java"
        val writer = PrintWriter(path, "UTF-8")
        writer.println("package lox;")
        writer.println()
        writer.println("import java.util.List;")
        writer.println()
        writer.println("abstract class $baseName {")

        // The AST classes.
        for (type in types) {
            val className = type.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[0].trim { it <= ' ' }
            val fields = type.split(":".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1].trim { it <= ' ' }
            defineType(writer, baseName, className, fields)
        }

        writer.println("}")
        writer.close()
    }

    private fun defineType(
        writer: PrintWriter, baseName: String,
        className: String, fieldList: String
    ) {
        writer.println(
            "  static class " + className + " extends " +
                    baseName + " {"
        )

        // Constructor.
        writer.println("    $className($fieldList) {")

        // Store parameters in fields.
        val fields = fieldList.split(", ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()
        for (field: String in fields) {
            val name = field.split(" ".toRegex()).dropLastWhile { it.isEmpty() }.toTypedArray()[1]
            writer.println("      this.$name = $name;")
        }
        writer.println("    }")

        // Fields.
        writer.println()
        for (field: String in fields) {
            writer.println("    final $field;")
        }
        writer.println("  }")
    }
}
