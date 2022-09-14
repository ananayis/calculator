package com.parinaz.mycalculator

import java.lang.RuntimeException

class Calculator {

    private var str: String = ""
    private var pos = -1
    private var ch = 0

    private fun nextChar() {
        ch = if (++pos < str.length) str[pos].toInt() else -1
    }

    private fun eat(charToEat: Int): Boolean {
        while (ch == ' '.toInt()) nextChar()
        if (ch == charToEat) {
            nextChar()
            return true
        }
        return false
    }

    private fun parse(): Double {
        nextChar()
        val x = parseExpression()
        if (pos < str.length) throw RuntimeException("Unexpected: " + ch.toChar())
        return x
    }

    // Grammar:
    // expression = term | expression `+` term | expression `-` term
    // term = factor | term `*` factor | term `/` factor
    // factor = `+` factor | `-` factor | `(` expression `)` | number
    //        | functionName `(` expression `)` | functionName factor
    //        | factor `^` factor
    private fun parseExpression(): Double {
        var x = parseTerm()
        while (true) {
            if (eat('+'.toInt())) x += parseTerm() // addition
            else if (eat('-'.toInt())) x -= parseTerm() // subtraction
            else return x
        }
    }

    private fun parseTerm(): Double {
        var x = parseFactor()
        while (true) {
            if (eat('*'.toInt())) x *= parseFactor() // multiplication
            else if (eat('/'.toInt())) x /= parseFactor() // division
            else if (eat('%'.toInt())) x %= parseFactor()
            else return x
        }
    }

    private fun parseFactor(): Double {
        if (eat('+'.toInt())) return +parseFactor() // unary plus
        if (eat('-'.toInt())) return -parseFactor() // unary minus
        var x: Double
        val startPos = pos
        if (eat('('.toInt())) { // parentheses
            x = parseExpression()
            if (!eat(')'.toInt())) throw RuntimeException("Missing ')'")
        } else if (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) { // numbers
            while (ch >= '0'.toInt() && ch <= '9'.toInt() || ch == '.'.toInt()) nextChar()
            x = str.substring(startPos, pos).toDouble()
        } else if (ch >= 'a'.toInt() && ch <= 'z'.toInt()) { // functions
            while (ch >= 'a'.toInt() && ch <= 'z'.toInt()) nextChar()
            val func = str.substring(startPos, pos)
            if (eat('('.toInt())) {
                x = parseExpression()
                if (!eat(')'.toInt())) throw RuntimeException("Missing ')' after argument to $func")
            } else {
                x = parseFactor()
            }
            x =
                if (func == "sqrt") Math.sqrt(x) else if (func == "sin") Math.sin(
                    Math.toRadians(
                        x
                    )
                ) else if (func == "cos") Math.cos(
                    Math.toRadians(x)
                ) else if (func == "tan") Math.tan(Math.toRadians(x)) else throw RuntimeException(
                    "Unknown function: $func"
                )
        } else {
            throw RuntimeException("Unexpected: " + ch.toChar())
        }
        if (eat('^'.toInt())) x = Math.pow(x, parseFactor()) // exponentiation
        return x
    }

     fun calculate(str: String): String {
         this.str = str
        val result =  parse()
         return if (result.toInt().toDouble() == result) {
             result.toInt().toString()
         } else {
             result.toString()
         }
    }
}
