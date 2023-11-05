import lox.AstPrinter
import lox.Expr
import lox.Expr.*
import lox.Token
import lox.TokenType


fun main(args: Array<String>) {

//    Lox.init(args)
//    GenerateAst().init(arrayOf("src/main/kotlin/lox"))
    testAstPrinter()
}

private fun testAstPrinter() {
    val expression: Expr = Binary(
        Unary(
            Token(TokenType.MINUS, "-", null, 1),
            Literal(123)
        ),
        Token(TokenType.STAR, "*", null, 1),
        Grouping(
            Literal(45.67)
        )
    )

    println(AstPrinter().print(expression))
}