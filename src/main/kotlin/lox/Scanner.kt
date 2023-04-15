package lox

// lox.Scanner job is to take source and convert them to tokens
class Scanner(private val source: String) {
    private val tokens = mutableListOf<Token>()

    private var start = 0
    private var current = 0
    private var line = 1

    private val keywords = mapOf(
        "and" to TokenType.AND,
        "class" to TokenType.CLASS,
        "else" to TokenType.ELSE,
        "false" to TokenType.FALSE,
        "for" to TokenType.FOR,
        "fun" to TokenType.FUN,
        "if" to TokenType.IF,
        "nil" to TokenType.NIL,
        "or" to TokenType.OR,
        "print" to TokenType.PRINT,
        "return" to TokenType.RETURN,
        "super" to TokenType.SUPER,
        "this" to TokenType.THIS,
        "true" to TokenType.TRUE,
        "var" to TokenType.VAR,
        "while" to TokenType.WHILE,
    )


    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
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
        when {
            // Single character lexeme
            c == '(' -> addToken(TokenType.LEFT_PAREN)
            c == ')' -> addToken(TokenType.RIGHT_PAREN)
            c == '{' -> addToken(TokenType.LEFT_BRACE)
            c == '}' -> addToken(TokenType.RIGHT_BRACE)
            c == ',' -> addToken(TokenType.COMMA)
            c == '.' -> addToken(TokenType.DOT)
            c == '-' -> addToken(TokenType.MINUS)
            c == '+' -> addToken(TokenType.PLUS)
            c == ';' -> addToken(TokenType.SEMICOLON)
            c == '*' -> addToken(TokenType.STAR)
            // Single or double char lexeme
            c == '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            c == '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            c == '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            c == '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            // Longer lexeme
            c == '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(TokenType.SLASH)
                }
            }
            // Bypass meaningless lexemes
            c == ' ' || c == '\r' || c == '\t' -> {}
            c == '\n' -> line++
            // String literal
            c == '"' -> string()
            // Number literal
            c.isDigit() -> number()
            isAlpha(c) -> identifier()
            else -> Lox.error(line = line, message = "Unexpected character.")
        }
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()
        val text = source.substring(start, current)
        var type = keywords[text]
        if (type == null) type = TokenType.IDENTIFIER
        addToken(type)
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || c.isDigit()
    }

    private fun number() {
        while (peek().isDigit()) advance()

        // Look for a fractional part.
        if (peek() == '.' && peekNext().isDigit()) {
            // Consume the "."
            advance()
            while (peek().isDigit()) advance()
        }
        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) '\u0000' else source[current + 1]
    }

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++
            advance()
        }
        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.")
            return
        }

        // The closing "
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun peek(): Char {
        return if (isAtEnd()) '\u0000' else source[current]
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
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
