package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ruiqi.cisc600.Equations.Companion.getFactorial
import com.example.ruiqi.cisc600.Equations.Companion.getNthDerivativeTermOfLn
import com.example.ruiqi.cisc600.Equations.Companion.getNthDerivativeTermOfLnInString
import com.example.ruiqi.cisc600.Equations.Companion.getTruePercentRelativeError
import com.example.ruiqi.cisc600.Equations.Companion.getTrueValueOfLn
import kotlinx.android.synthetic.main.fragment_m04_2.view.*
import kotlin.math.pow

/**
 * Fragment for M04 Hands-on & Drills part 2.
 * Problem 4.6
 * To predict f(2.5) for f(x)=ln(x) using zero- through fourth-order of Taylor series expansions with base point x = 1, the solution needs to:
 *     1. calculate the step size;
 *     2. calculate true value of the ln(2.5);
 *     3. calculate zero-order approximation with its true percent relative error;
 *     4. add the first derivative term for the first-order approximation, and calculate the true percent relative error;
 *     5. continue the process until the fourth-order approximation and its true percent relative error are calculated.
 * The process and the final result will be printed out.
 */
class M042Fragment : Fragment() {

    companion object {
        const val x0 = 1
        const val x1 = 2.5
        const val h = 2.5 - 1 // step size
        lateinit var table: TextView // result view
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_m04_2, container, false)

        table = view.findViewById(R.id.table)
        view.button.setOnClickListener {
            // clear data
            table.text = ""
            // check the given significant figures
            val order = view.orderView.text.toString().toIntOrNull()
            if (checkInput(order)) { // start processing
                solve(order!!)
            }
        }
        view.cancelBtn.setOnClickListener {
            table.text = ""
        }

        return view
    }

    private fun checkInput(order: Int?) : Boolean {
        var result = true
        val text = StringBuilder()
        order?.let {
            if (order <= 0) {
                result = false
                text.append("Please enter a valid number for the order!\n")
            }
        } ?: run {
            result = false
            text.append("Please enter a valid number for the order!\n")
        }
        table.text = text.toString()
        return result
    }

    private fun solve(order: Int) {
        // get the true value of ln(x)
        val vt = getTrueValueOfLn(x1)
        table.append("True value of ln($x1): $vt \n\n")

        // create result table header
        printRow("Order", "f(n)(x)", "f($x1)", "et(%)")

        // initial the calculation
        var result = 0.0 // the estimate result
        var et = 0.0 // true percent relative error
        for (index in 0..order) {
            val term = getNthDerivativeTermOfLnInString(index)
            result += getNthDerivativeTermOfLn(x0.toDouble(), index) * h.pow(index) / getFactorial(index)
            et = getTruePercentRelativeError(vt, result)
            printRow(index.toString(), term, result.toString(), et.toString())
        }
    }

    private fun printRow(order: String, term: String, result: String, et: String) {
        val text = StringBuilder().apply {
            append(order.padStart(5, ' '))
            append(" ")
        }
        if (term.length > 20) {
            text.append(term.take(20))
        } else {
            text.append(term.padStart(20, ' '))
        }
        text.append(" ")
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
        text.append("\n")
        table.append(text.toString())
    }
}