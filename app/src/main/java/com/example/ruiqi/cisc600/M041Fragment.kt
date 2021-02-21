package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ruiqi.cisc600.Equations.Companion.getEstPercentRelativeError
import com.example.ruiqi.cisc600.Equations.Companion.getNthMTermOfArctan
import com.example.ruiqi.cisc600.Equations.Companion.getTruePercentRelativeError
import com.example.ruiqi.cisc600.Equations.Companion.getTrueValueOfArctangent
import kotlinx.android.synthetic.main.fragment_m04_1.view.*
import kotlin.math.PI
import kotlin.math.absoluteValue

/**
 * Fragment for M04 Hands-on & Drills part 1.
 * Problem 4.4
 * To determine the number of terms necessary to approximate arctan(PI/6), the solution needs to:
 *     1. print out the first four terms;
 *     2. calculate true value of the arctan(PI/6);
 *     3. calculate pre-specified percent tolerance based on the given significant figures;
 *     4. add terms from the given Maclaurin series expansion one at a time to estimate arctan(PI/6);
 *     5. after each new term is added, compute the true and approximate percent relative errors respectively;
 *     6. continue step 3&4 until the absolute value of the approximate error estimate fails below the percent tolerance.
 * The process and the final result will be printed out.
 * To get more accurate result, Double is used during calculation.
 * During display, all error percentage numbers are shown in Float to avoid possible representation errors.
 */
class M041Fragment : Fragment() {

    companion object {
        const val x = PI / 6
        lateinit var table: TextView // result view
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_m04_1, container, false)

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
        // print out the first four terms
        table.append("arctan x = x - (x^3)/3 + (x^5)/5 - (x^7)/7 + …\n\n")

        // get the true value of arctan(x)
        val vt = getTrueValueOfArctangent(x)
        table.append("True value of arctan(π/6): $vt \n\n")

        // get the pre-specified percent tolerance
        val es = Equations.getPercentTolerance(figure)
        table.append("Error criterion (%): ${es.toFloat()} \n\n")

        // create result table header
        printRow("Terms", "Result", "et(%)", "ea(%)")

        // initial the calculation
        var index = 1 // number of current terms
        var result = getNthMTermOfArctan(x, index - 1) // the estimate result
        var oldResult = result // contain the previous result
        var et = getTruePercentRelativeError(vt, result) // true percent relative error
        var ea = Double.NaN // approximate percent relative error, initialized as the max value of Double
        printRow(index.toString(), result.toString(), et.toFloat().toString(), ea.toString())

        // add a new term to repeat calculation while error criterion is not satisfied
        do {
            index++
            oldResult = result
            result += getNthMTermOfArctan(x, index - 1)
            et = getTruePercentRelativeError(vt, result)
            ea = getEstPercentRelativeError(oldResult, result)
            printRow(index.toString(), result.toString(), et.toFloat().toString(), ea.toFloat().toString())
        } while (ea.absoluteValue >= es)
    }

    private fun printRow(term: String, result: String, et: String, ea: String) {
        val text = StringBuilder().apply {
            append(term.padStart(5, ' '))
            append(" ")
        }
        if (result.length > 20) {
            text.append(result.take(20))
        } else {
            text.append(result.padStart(20, ' '))
        }
        text.append(" ")
        if (et.length > 20) {
            text.append(et.take(20))
        } else {
            text.append(et.padStart(20, ' '))
        }
        text.append(" ")
        if (ea.length > 20) {
            text.append(ea.take(20))
        } else {
            text.append(ea.padStart(20, ' '))
        }
        text.append("\n")
        table.append(text.toString())
    }
}