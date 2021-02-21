package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ruiqi.cisc600.Equations.Companion.getEstPercentRelativeError
import com.example.ruiqi.cisc600.Equations.Companion.getNthTermOfCos
import com.example.ruiqi.cisc600.Equations.Companion.getPercentTolerance
import com.example.ruiqi.cisc600.Equations.Companion.getTruePercentRelativeError
import com.example.ruiqi.cisc600.Equations.Companion.getTrueValueOfCos
import kotlinx.android.synthetic.main.fragment_m03.view.*
import kotlin.math.PI
import kotlin.math.absoluteValue

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
        val vt = getTrueValueOfCos(x)
        table.append("True value of cos(0.3π): $vt \n\n")

        // get the pre-specified percent tolerance
        val es = getPercentTolerance(figure)
        table.append("Error criterion (%): ${es.toFloat()} \n\n")

        // create result table header
        printRow("Terms", "Result", "et(%)", "ea(%)")

        // initial the calculation
        var index = 1 // number of current terms
        var result = getNthTermOfCos(x, index - 1) // the estimate result
        var oldResult = result // contain the previous result
        var et = getTruePercentRelativeError(vt, result) // true percent relative error
        var ea = Double.MAX_VALUE // approximate percent relative error, initialized as the max value of Double
        printRow(index.toString(), result.toString(), et.toFloat().toString(), "-")

        // add a new term to repeat calculation while error criterion is not satisfied
        do {
            index++
            oldResult = result
            result += getNthTermOfCos(x, index - 1)
            et = getTruePercentRelativeError(vt, result)
            ea = getEstPercentRelativeError(oldResult, result)
            printRow(index.toString(), result.toString(), et.toFloat().toString(), ea.toFloat().toString())
        } while (ea.absoluteValue >= es)
    }

    private fun printRow(term: String, result: String, et: String, ea: String) {
        table.append(String.format("%-10s %-20s %-20s %-20s \n", term, result, et, ea))
    }

}