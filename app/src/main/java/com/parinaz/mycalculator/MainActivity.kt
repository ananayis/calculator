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
    private val calculator = Calculator()
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

        binding.button2.setOnClickListener {
            equation += "2"
        }

        binding.button3.setOnClickListener {
            equation += "3"
        }

        binding.button4.setOnClickListener {
            equation += "4"
        }

        binding.button5.setOnClickListener {
            equation += "5"
        }

        binding.button6.setOnClickListener {
            equation += "6"
        }

        binding.button7.setOnClickListener {
            equation += "7"
        }

        binding.button8.setOnClickListener {
            equation += "8"
        }

        binding.button9.setOnClickListener {
            equation += "9"
        }

        binding.button0.setOnClickListener {
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


        binding.buttonMultiplication.setOnClickListener {
            if (equation.isNotEmpty() && !isLastOperator()){
                equation += "*"
            }
        }

        binding.buttonMinus.setOnClickListener {
            if (equation.isNotEmpty() && !isLastOperator()){
                equation += "-"
            }
        }

        binding.buttonSum.setOnClickListener {
            if (equation.isNotEmpty() && !isLastOperator()){
                equation += "+"
            }
        }

        //buttonEqual.setBackground(drawable)
        binding.buttonEqual.setOnClickListener {
            if (equation.isNotEmpty()) {
                try {
                    equation = calculator.calculate(equation)
                }
                catch (e: RuntimeException) {
                    Toast.makeText(this, "Invalid format used.", Toast.LENGTH_SHORT).show()
                }
            }
        }

        binding.buttonClear.setOnClickListener {
            equation = ""
        }

        binding.buttonBackSpace.setOnClickListener {
            equation = equation.dropLast(1)
        }

        binding.buttonPercent.setOnClickListener {
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
}
