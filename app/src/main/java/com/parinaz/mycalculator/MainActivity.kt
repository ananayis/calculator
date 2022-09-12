package com.parinaz.mycalculator

import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.TextView
import android.widget.Toast
import com.parinaz.mycalculator.databinding.ActivityMainBinding
import org.w3c.dom.Text
import java.lang.RuntimeException

class MainActivity : AppCompatActivity() {
    private val binding: ActivityMainBinding by lazy { ActivityMainBinding.inflate(layoutInflater) }

    private var equation = ""
        set(value) {
            field = value
            binding.textAnswer.text = value
        }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(binding.root)

        binding.button1.setOnClickListener {
            equation += "1"
        }

        binding.button2.setOnClickListener(){
            equation += "2"
        }

        binding.button3.setOnClickListener(){
            equation += "3"
        }

        binding.button4.setOnClickListener() {
            equation += "4"
        }

        binding.button5.setOnClickListener(){
            equation += "5"
        }

        binding.button6.setOnClickListener(){
            equation += "6"
        }

        binding.button7.setOnClickListener(){
            equation += "7"
        }

        binding.button8.setOnClickListener(){
            equation += "8"
        }

        binding.button9.setOnClickListener(){
            equation += "9"
        }

        binding.button0.setOnClickListener(){
            equation += "0"
        }

        binding.buttonDot.setOnClickListener {
            if (equation.isNotEmpty() && !hasLastNumberDot() && !isLastOperator()) {
                equation += "."
            }
        }

        binding.buttonDivision.setOnClickListener {
           if (equation.isNotEmpty() && !isLastOperator()){
               equation += "/"
           }
        }


        binding.buttonMultiplication.setOnClickListener(){
            if (equation.isNotEmpty() && !isLastOperator()){
                equation += "*"
            }
        }

        binding.buttonMinus.setOnClickListener(){
            if (equation.isNotEmpty() && !isLastOperator()){
                equation += "-"
            }
        }

        binding.buttonSum.setOnClickListener(){
            if (equation.isNotEmpty() && !isLastOperator()){
                equation += "+"
            }
        }

        //buttonEqual.setBackground(drawable)
        binding.buttonEqual.setOnClickListener {
            if (equation.isNotEmpty()) {
                try {
                    val result = eval(equation)
                    if (result.toInt().toDouble() == result) {
                        equation = result.toInt().toString()
                    } else {
                        equation = result.toString()
                    }
                }
                catch (e: RuntimeException) {
                    Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.buttonClear.setOnClickListener(){
            equation = ""
        }

        binding.buttonBackSpace.setOnClickListener(){
            equation = equation.dropLast(1)
//            if (textAnswer.length() > 0) {
//                textAnswer.text = textAnswer.text.substring(0..textAnswer.length() - 2)
//            }
        }

        binding.buttonPercent.setOnClickListener(){
            if (equation.isNotEmpty() && !isLastOperator()){
                equation += "%"
            }

        }
    }

    private fun isLastOperator(): Boolean {
        return equation.isNotEmpty() && (equation.last() == '/' || equation.last() == '*' || equation.last() == '+' || equation.last() == '-' || equation.last() == '.' || equation.last() == '%')
    }

    private fun isOperator(c: Char): Boolean {
        return c == '+' || c == '-' || c == '/' || c == '*' || c == '%'
    }

    private fun hasLastNumberDot(): Boolean {
        equation.reversed().forEach {
            if (it == '.') return true
            if (isOperator(it)) return false
        }
        return false
    }

    private fun eval(str: String): Double {
        return object : Any() {
            var pos = -1
            var ch = 0
            fun nextChar() {
                ch = if (++pos < str.length) str[pos].toInt() else -1
            }

            fun eat(charToEat: Int): Boolean {
                while (ch == ' '.toInt()) nextChar()
                if (ch == charToEat) {
                    nextChar()
                    return true
                }
                return false
            }

            fun parse(): Double {
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
            fun parseExpression(): Double {
                var x = parseTerm()
                while (true) {
                    if (eat('+'.toInt())) x += parseTerm() // addition
                    else if (eat('-'.toInt())) x -= parseTerm() // subtraction
                    else return x
                }
            }

            fun parseTerm(): Double {
                var x = parseFactor()
                while (true) {
                    if (eat('*'.toInt())) x *= parseFactor() // multiplication
                    else if (eat('/'.toInt())) x /= parseFactor() // division
                    else return x
                }
            }

            fun parseFactor(): Double {
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
        }.parse()
    }
}
