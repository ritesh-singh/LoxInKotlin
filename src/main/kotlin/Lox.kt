import java.io.BufferedReader
import java.io.InputStreamReader
import java.nio.charset.Charset
import java.nio.file.Files
import java.nio.file.Path
import kotlin.system.exitProcess

object Lox {

    private var hadError = false

    fun init(args: Array<String>) {
        when {
            args.size > 1 -> {
                println("Usage: jlox [script]")
                exitProcess(64)
            }

            args.size == 1 -> {
                runFile(args[0])
            }

            else -> {
                runPrompt()
            }
        }
    }

    private fun runFile(path: String) {
        val bytes: ByteArray = Files.readAllBytes(Path.of(path))
        run(bytes.toString(Charset.defaultCharset()))
        if (hadError) exitProcess(65)
    }

    private fun runPrompt() {
        val input = InputStreamReader(System.`in`)
        val reader = BufferedReader(input)
        while (true) {
            print("> ")
            val line = reader.readLine() ?: break
            run(line)
            hadError = false
        }
    }

    private fun run(source: String) {
        val scanner = Scanner(source)
        val tokens: List<Token> = scanner.scanTokens()

        for (token in tokens) {
            println(token)
        }
    }

    fun error(line: Int, message: String) {
        report(line = line, where = "", message = message)
    }

    private fun report(line: Int, where: String, message: String) {
        System.err.println(
            "[line $line ] Error $where : $message"
        )
        hadError = false
    }
}