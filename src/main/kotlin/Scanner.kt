// Scanner job is to take source and convert them to tokens
class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()

    private var start = 0
    private var current = 0
    private var line = 1

    fun scanTokens():List<Token>{
        while (!isAtEnd()){
            start = current
            scanToken()
        }
        tokens.add(
            Token(
                type = TokenType.EOF,
                lexeme = "",
                literal = null,
                line = line
            )
        )

        return tokens
    }

    private fun scanToken() {
        val c = advance()
        when (c) {
            '(' -> addToken(TokenType.LEFT_PAREN)
            ')' -> addToken(TokenType.RIGHT_PAREN)
            '{' -> addToken(TokenType.LEFT_BRACE)
            '}' -> addToken(TokenType.RIGHT_BRACE)
            ',' -> addToken(TokenType.COMMA)
            '.' -> addToken(TokenType.DOT)
            '-' -> addToken(TokenType.MINUS)
            '+' -> addToken(TokenType.PLUS)
            ';' -> addToken(TokenType.SEMICOLON)
            '*' -> addToken(TokenType.STAR)
            else -> Lox.error(line = line, message = "Unexpected character.")
        }
    }

    private fun advance(): Char = source[current++]

    private fun addToken(tokenType: TokenType) = addToken(tokenType, null)

    private fun addToken(tokenType: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(
            Token(
                type = tokenType,
                lexeme = text,
                literal = literal,
                line = line
            )
        )
    }

    private fun isAtEnd(): Boolean = current >= source.length
}
