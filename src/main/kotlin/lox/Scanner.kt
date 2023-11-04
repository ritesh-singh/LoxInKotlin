package lox

class Scanner(private val source: String) {

    private val tokens = mutableListOf<Token>()

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

    // Points to the first character in the lexeme being scanned
    private var start = 0

    // Points at the character being considered
    private var current = 0

    // Tracks what source line current is on, so we can produce tokens that know their location.
    private var line = 1

    fun scanTokens(): List<Token> {
        while (!isAtEnd()) {
            // We are beginning of the next lexeme.
            start = current
            scanToken();
        }

        // Appends one final token, with EOF
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

    // Tells us if we have consumed all the characters
    private fun isAtEnd(): Boolean {
        return current >= source.length
    }

    private fun scanToken() {
        val c: Char = advance()
        when (c) {
            // Single character lexeme
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

            // Operators
            '!' -> addToken(if (match('=')) TokenType.BANG_EQUAL else TokenType.BANG)
            '=' -> addToken(if (match('=')) TokenType.EQUAL_EQUAL else TokenType.EQUAL)
            '<' -> addToken(if (match('=')) TokenType.LESS_EQUAL else TokenType.LESS)
            '>' -> addToken(if (match('=')) TokenType.GREATER_EQUAL else TokenType.GREATER)
            '/' -> {
                if (match('/')) {
                    // A comment goes until the end of the line.
                    while (peek() != '\n' && !isAtEnd()) advance()
                } else {
                    addToken(TokenType.SLASH)
                }
            }

            ' ', '\r', '\t' -> {
                // Ignore whitespace
            }

            '\n' -> line++

            // String literals
            '"' -> string()

            else -> {
                // Number literals
                if (isDigit(c)) {
                    number()
                } else if (isAlpha(c)) { // Reserved words and identifiers
                    identifier()
                } else {
                    // Unexpected character
                    Lox.error(line = line, message = "Unexpected character.")
                }
            }
        }
    }

    private fun advance(): Char = source[current++]

    private fun addToken(type: TokenType) = addToken(type = type, literal = null)

    private fun addToken(type: TokenType, literal: Any?) {
        val text = source.substring(start, current)
        tokens.add(Token(type = type, lexeme = text, literal = literal, line = line))
    }

    private fun match(expected: Char): Boolean {
        if (isAtEnd()) return false
        if (source[current] != expected) return false
        current++
        return true
    }

    // Look ahead - on character lookahead
    private fun peek(): Char = if (isAtEnd()) '\u0000' else source[current]

    private fun string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n') line++;
            advance();
        }

        if (isAtEnd()) {
            Lox.error(line, "Unterminated string.");
            return;
        }

        // The closing ".
        advance()

        // Trim the surrounding quotes.
        val value = source.substring(start + 1, current - 1)
        addToken(TokenType.STRING, value)
    }

    private fun isDigit(c: Char): Boolean = c in '0'..'9'

    private fun number() {
        while (isDigit(peek())) advance()

        // Look for a fractional part.
        if (peek() == '.' && isDigit(peekNext())) {
            // Consume the "."
            advance()
            while (isDigit(peek())) advance()
        }
        addToken(TokenType.NUMBER, source.substring(start, current).toDouble())
    }

    private fun peekNext(): Char {
        return if (current + 1 >= source.length) '\u0000' else source[current + 1]
    }

    private fun identifier() {
        while (isAlphaNumeric(peek())) advance()
        val text = source.substring(start, current)
        var type: TokenType? = keywords[text]
        if (type == null) type = TokenType.IDENTIFIER
        addToken(type)
    }

    private fun isAlpha(c: Char): Boolean {
        return c in 'a'..'z' || c in 'A'..'Z' || c == '_'
    }

    private fun isAlphaNumeric(c: Char): Boolean {
        return isAlpha(c) || isDigit(c)
    }
}