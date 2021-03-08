package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ruiqi.cisc600.Equations.Companion.getEstPercentRelativeErrorForBracket
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_m05.view.*
import kotlin.math.absoluteValue
import kotlin.math.pow

/**
 * Fragment for M05 Hands-on & Drills part 1.
 * Problem 5.3
 * To determine the real root of f(x) = -25 + 82x - 90x^2 + 44x^3 - 8x^4 + 0.7x^5, the solution needs to :
 *     1. import GraphView library and add GraphView to the layout;
 *     2. generate a series of data points for the given function, which should cover the given interval [0.5, 1.0];
 *     3. use the GraphView library to draw the graph in the GraphView based on the data point series;
 *     4. set lower bound and upper bound of the initial guess;
 *     5. for bisection method,
 *         a) check the sign of the function over the interval at both end points;
 *         b) evaluate the function value at the midpoint;
 *         c) the location of root is determined as lying at the midpoint of the sub-interval within which the sign change occurs;
 *         d) repeat the above process until approximate percent relative error is less than the pre-specified percent tolerance;
 *     6. for false-position method,
 *         a) join f(xl) and f(xu) by a straight line and find its intersection with the x-axis as the estimate of the root;
 *         b) evaluate the function value at the estimate root;
 *         c) use the new point to replace one of the end points to keep them on opposite sides of the x-axis;
 *         d) repeat the above process until approximate percent relative error is less than the pre-specified percent tolerance.
 * The process and the final result will be printed out.
 * To get more accurate result, Double is used during calculation.
 * During display, all error percentage numbers are shown in Float to avoid possible representation errors.
 */
class M05Fragment : Fragment() {

    companion object {
        const val lb = 0.5 // lower bound of initial guess
        const val ub = 1.0 // upper bound of initial guess
        const val es1 = 10.0 // percent tolerance for question (b)
        const val es2 = 0.2 // percent tolerance for question (c)
        lateinit var graphView: GraphView
        lateinit var table: TextView // result view
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_m05, container, false)

        graphView = view.findViewById(R.id.graphView)
        table = view.findViewById(R.id.table)
        view.button.setOnClickListener {
            // clear data
            table.text = ""
            graphView.removeAllSeries()
            // start solution
            solve()
        }

        return view
    }

    private fun solve() {
        // draw the graph for the given function
        draw()
        // find the root using bisection method
        bisection()
        // find the root using false-position method
        falsePosition()
    }

    private fun draw() {
        // set manual X bounds
        graphView.viewport.apply {
            isXAxisBoundsManual = true
            setMinX(0.0)
            setMaxX(2.0)
        }
        // generate a series of data points that covers [0.0, 1.5]
        val series = LineGraphSeries<DataPoint>()
        var x = 0.0 // starting point on x axis
        var y: Double
        while (x <= 2.0) {
            y = calculateFun(x)
            series.appendData(DataPoint(x, y), true, 200)
            x += 0.1
        }
        graphView.addSeries(series)
        graphView.visibility = View.VISIBLE
        table.append("As we can see from the graph, the approximate root is x = 0.5.\n")
    }

    private fun bisection() {
        table.append("\nBisection Method:\n")
        printRow("Iteration", "xl", "xu", "xr", "ea")
        var iter = 0 // iteration
        var xl = lb // lower bound
        var xu = ub // upper bound
        var xr: Double // midpoint
        var ea: Double // approximate percent relative error
        var fl = calculateFun(xl) // function value at xl
        var fr: Double // function value at xr
        var test: Double
        do {
            iter++
            xr = (xl + xu) / 2
            fr = calculateFun(xr)
            ea = getEstPercentRelativeErrorForBracket(xl, xu)
            printRow(iter.toString(), xl.toString(), xu.toString(), xr.toString(), ea.toFloat().toString())
            test = fl * fr
            if (test < 0) {
                xu = xr
            } else if (test > 0) {
                xl = xr
                fl = fr
            } else { // xr is the root
                ea = 0.0
            }
        } while (ea.absoluteValue >= es1)
        table.append("f($xr)=$fr")
        val analysis = StringBuilder("\n").apply {
            append("In general, bisection method is slower than other methods. ")
            append("For example, we can compare the approximate root of 4th iteration with the below one.\n")
        }.toString()
        table.append(analysis)
    }

    private fun falsePosition() {
        table.append("\nFalse-Position Method:\n")
        printRow("Iteration", "xl", "xu", "xr", "ea")
        var iter = 0 // iteration
        var xl = lb // lower bound
        var xu = ub // upper bound
        var xr: Double // midpoint
        var ea: Double // approximate percent relative error
        var fl = calculateFun(xl) // function value at xl
        var fu = calculateFun(xu)  // function value at xu
        var fr: Double  // function value at xr
        var test: Double
        var iu = 0 // index of upper bound
        var il = 0 // index of lower bound
        do {
            iter++
            xr = xu - (fu*(xl - xu)/(fl - fu)) // Eq.(5.7)
            fr = calculateFun(xr)
            ea = getEstPercentRelativeErrorForBracket(xl, xu)
            printRow(iter.toString(), xl.toFloat().toString(), xu.toFloat().toString(), xr.toFloat().toString(), ea.toFloat().toString())
            test = fl * fr
            if (test < 0) {
                xu = xr
                fu = fr
                iu = 0
                il++
                if (il >= 2) {
                    fl /= 2
                }
            } else if (test > 0) {
                xl = xr
                fl = fr
                il = 0
                iu++
                if (iu >= 2) {
                    fu /= 2
                }
            } else { // xr is the root
                ea = 0.0
            }
        } while (ea.absoluteValue >= es2)
        table.append("f($xr)=$fr")
        val analysis = StringBuilder("\n").apply {
            append("The original false-position method performs poorly for this problem since its graph has significant curvature. ")
            append("And it needs 20 iterations to solve the problem. ")
            append("To mitigate the one-sided nature, the algorithm should detect when one of the bounds is stuck. ")
            append("And the function value at the stagnant bound can be divided in half. ")
            append("After apply the modified false-position method, only 7 iterations is needed to determine the root. ")
        }.toString()
        table.append(analysis)
    }

    // calculate the function value based on the given x
    private fun calculateFun(x: Double) : Double {
        return -25 + 82*x - 90*x.pow(2) + 44*x.pow(3) - 8*x.pow(4) + 0.7*x.pow(5)
    }

    private fun printRow(iter: String, xl: String, xu: String, xr: String, ea: String) {
        val text = StringBuilder().apply {
            append(iter.padStart(5, ' '))
            append(" ")
            append(xl.padStart(15, ' '))
            append(" ")
            append(xu.padStart(15, ' '))
            append(" ")
            append(xr.padStart(15, ' '))
            append(" ")
            append(ea.padStart(15, ' '))
            append("\n")
        }
        table.append(text.toString())
    }
}