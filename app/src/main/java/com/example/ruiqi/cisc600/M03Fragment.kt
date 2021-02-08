package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import kotlinx.android.synthetic.main.fragment_m03.view.*
import kotlin.math.PI
import kotlin.math.absoluteValue
import kotlin.math.cos
import kotlin.math.pow

/**
 * Fragment for M03 Hands-on & Drills.
 * Problem 3.10
 * To determine the number of terms necessary to approximate cos(0.3*PI), the solution needs to:
 *     1. calculate true value of the cos(0.3*PI);
 *     2. calculate pre-specified percent tolerance based on the given significant figures;
 *     3. add terms from the given Maclaurin series expansion one at a time to estimate cos(0.3*PI);
 *     4. after each new term is added, compute the true and approximate percent relative errors respectively;
 *     5. continue step 3&4 until the absolute value of the approximate error estimate fails below the percent tolerance.
 * The process and the final result will be printed out.
 * To get more accurate result, Double is used during calculation.
 * During display, all error percentage numbers are shown in Float to avoid possible representation errors.
 */
class M03Fragment : Fragment() {

    companion object {
        lateinit var table: TextView // result view
        const val x = 0.3 * PI
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view  = inflater.inflate(R.layout.fragment_m03, container, false)

        table = view.findViewById(R.id.table)
        view.button.setOnClickListener {
            // clear data
            table.text = ""
            // check the given significant figures
            val figure = view.figureView.text.toString().toIntOrNull()
            if (checkInput(figure)) { // start processing
                solve(figure!!)
            }
        }
        view.cancelBtn.setOnClickListener {
            table.text = ""
        }

        return view
    }

    private fun checkInput(figure: Int?) : Boolean {
        var result = true
        val text = StringBuilder()
        figure?.let {
            if (figure <= 0) {
                result = false
                text.append("Please enter a valid number for significant figures!\n")
            }
        } ?: run {
            result = false
            text.append("Please enter a valid number for significant figures!\n")
        }
        table.text = text.toString()
        return result
    }

    private fun solve(figure: Int) {
        // get the true value of cos(x)
        val vt = getTrueValue(x)
        table.append("True value of cos(0.3π): $vt \n\n")

        // get the pre-specified percent tolerance
        val es = getPercentTolerance(figure)
        table.append("Error criterion (%): ${es.toFloat()} \n\n")

        // create result table header
        printRow("Terms", "Result", "et(%)", "ea(%)")

        // initial the calculation
        var index = 1 // number of current terms
        var result = getNthTerm(x, index - 1) // the estimate result
        var oldResult = result // contain the previous result
        var et = getTruePercentRelativeError(vt, result) // true percent relative error
        var ea = Double.MAX_VALUE // approximate percent relative error, initialized as the max value of Double
        printRow(index.toString(), result.toString(), et.toFloat().toString(), "-")

        // add a new term to repeat calculation while error criterion is not satisfied
        do {
            index++
            oldResult = result
            result += getNthTerm(x, index - 1)
            et = getTruePercentRelativeError(vt, result)
            ea = getEstPercentRelativeError(oldResult, result)
            printRow(index.toString(), result.toString(), et.toFloat().toString(), ea.toFloat().toString())
        } while (ea.absoluteValue >= es)
    }

    private fun printRow(term: String, result: String, et: String, ea: String) {
        table.append(String.format("%-10s %-20s %-20s %-20s \n", term, result, et, ea))
    }

    // Calculate the approximate percent relative error according to Eq.(3.5)
    // oldValue - the previous approximation
    // newValue - the current approximation
    private fun getEstPercentRelativeError(oldValue: Double, newValue: Double) : Double {
        return (newValue-oldValue) / newValue * 100
    }

    // Calculate the true percent relative error according to Eq.(3.3)
    // vt - the true value
    // ve - the estimate value
    private fun getTruePercentRelativeError(vt: Double, ve: Double) : Double {
        return (vt-ve) / vt * 100
    }

    // Get the n-th term of the given Maclaurin series expansion
    // (-1)^n * x^(2*n) / (2*n)!
    // x - the given parameter for cos function
    // n - the index of the term to be calculated, starting from 0
    private fun getNthTerm(x: Double, n: Int) : Double {
        return (-1).toDouble().pow(n) * x.pow(2*n) / getFactorial(2*n)
    }

    // Calculate factorial of a number
    // n - the given number to calculate n!
    private fun getFactorial(n: Int) : Int {
        var result = 1
        for (i in 1..n) {
            result *= i
        }
        return result
    }

    // Get the true value by using mathematical functions and constants from package kotlin.math
    // x - the given parameter for cos function
    private fun getTrueValue(x: Double) : Double {
        return cos(x)
    }

    // Get the percent tolerance according to Eq.(3.7)
    // n - number of significant figures
    private fun getPercentTolerance(n: Int) : Double {
        return (0.5 * 10.toDouble().pow(2 - n)) * 100
    }
}