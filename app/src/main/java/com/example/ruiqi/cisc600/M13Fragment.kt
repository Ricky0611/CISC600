package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_m06.view.*
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Fragment for M13 Hands-on & Drills (Problem 13.8).
 * The process and the final result will be printed out.
 * To get more accurate result, Double is used during calculation.
 */
class M13Fragment : Fragment() {

    companion object {
        const val es = 1.0 // percent tolerance
        val r = (sqrt(5.0) - 1)/2 // golden ratio
    }

    lateinit var graphView: GraphView
    lateinit var table: TextView // result view

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.fragment_m13, container, false)

        graphView = v.findViewById(R.id.graphView)
        table = v.findViewById(R.id.table)

        v.button.setOnClickListener {
            // clear data
            clear()
            // start solution
            solve()
        }

        return v
    }

    private fun solve() {
        // draw the function
        draw()
        // golden-section search
        goldenSection()
        // parabolic interpolation
        parabolicInterpolation()
        // Newton's method
        newtonsMethod()
    }

    private fun newtonsMethod() {
        table.append("\nNewton’s method (x0=-1, es=1%):\n")
        table.append("i, x, f(x), f'(x), f\"(x)\n")
        var x0 = -1.0
        var fx = f(x0) // function value
        var dfx = df(x0) // the first derivative of the function
        var d2fx = d2f(x0) // the second derivative of the function
        var iter = 1 // iteration
        table.append("$iter, $x0, $fx, $dfx, $d2fx\n")
        var x1: Double
        var ea = Double.POSITIVE_INFINITY // approximate percent relative error
        do {
            x1 = x0 - dfx/d2fx
            if (x1 != 0.0) {
                ea = Equations.getEstPercentRelativeError(x0, x1)
            }
            iter++
            x0 = x1
            fx = f(x0)
            dfx = df(x0)
            d2fx = d2f(x0)
            table.append("$iter, $x0, $fx, $dfx, $d2fx\n")
        } while (ea > es)
        table.append("f($x0)=$fx\n")
    }

    private fun parabolicInterpolation() {
        table.append("\nParabolic interpolation (x0=-2, x1=-1, x2=1, es=1%):\n")
        table.append("i, x0, f(x0), x1, f(x1), x2, f(x2), x3, f(x3)\n")
        var x0 = -2.0
        var x1 = -1.0
        var x2 = 1.0
        var f0 = f(x0)
        var f1 = f(x1)
        var f2 = f(x2)
        var iter = 1 // iteration
        var ea = Double.POSITIVE_INFINITY // approximate percent relative error
        // x value corresponds to the maximum value of the parabolic fit to the guesses
        var x3 = (f0*(x1*x1-x2*x2) + f1*(x2*x2-x0*x0) + f2*(x0*x0-x1*x1))/(2*f0*(x1-x2) + 2*f1*(x2-x0) + 2*f2*(x0-x1))
        var f3 = f(x3) // function value of x3
        table.append("$iter, $x0, $f0, $x1, $f1, $x2, $f2, $x3, $f3\n")
        do {
            iter++
            x0 = x1
            f0 = f1
            x1 = x2
            f1 = f2
            x2 = x3
            f2 = f3
            x3 = (f0*(x1*x1-x2*x2) + f1*(x2*x2-x0*x0) + f2*(x0*x0-x1*x1))/(2*f0*(x1-x2) + 2*f1*(x2-x0) + 2*f2*(x0-x1))
            f3 = f(x3)
            table.append("$iter, $x0, $f0, $x1, $f1, $x2, $f2, $x3, $f3\n")
            if (x3 != 0.0) {
                ea = Equations.getEstPercentRelativeError(x2, x3)
            }
        } while (ea > es)
        table.append("f($x3)=$f3\n")
    }

    private fun goldenSection() {
        table.append("\nGolden-section search (xl=-2, xu=1, es=1%):\n")
        table.append("i, xl, f(xl), x2, f(x2), x1, f(x1), xu, f(xu), d\n")
        var xl = -2.0 // lower bound
        var xu = 1.0 // upper bound
        var xint: Double // init difference
        var xopt: Double // x value for optimum
        var fx: Double // optimum result
        var ea = Double.POSITIVE_INFINITY // approximate percent relative error
        var iter = 1 // iteration
        var d = r * (xu - xl) // difference based on golden ratio
        var x1 = xl + d
        var x2 = xu - d
        var f1 = f(x1)
        var f2 = f(x2)
        if (f1 > f2) {
            xopt = x1
            fx = f1
        } else {
            xopt = x2
            fx = f2
        }
        table.append("$iter, $xl, ${f(xl)}, $x2, $f2, $x1, $f1, $xu, ${f(xu)}, $d\n")
        do {
            d *= r
            xint = xu - xl
            if (f1 > f2) {
                xl = x2
                x2 = x1
                x1 = xl + d
                f2 = f1
                f1 = f(x1)
            } else {
                xu = x1
                x1 = x2
                x2 = xu - d
                f1 = f2
                f2 = f(x2)
            }
            iter++
            if (f1 > f2) {
                xopt = x1
                fx = f1
            } else {
                xopt = x2
                fx = f2
            }
            table.append("$iter, $xl, ${f(xl)}, $x2, $f2, $x1, $f1, $xu, ${f(xu)}, $d\n")
            if (xopt != 0.0) {
                ea = (1-r) * (xint/xopt).absoluteValue * 100
            }
        } while (ea > es)
        table.append("f($xopt)=$fx\n")
    }

    private fun draw() {
        // set manual X bounds
        graphView.viewport.apply {
            isXAxisBoundsManual = true
            setMinX(-2.0)
            setMaxX(2.0)
        }
        // generate a series of data points that covers [-2.0, 2.0]
        val series = LineGraphSeries<DataPoint>()
        var x = -2.0 // starting point on x axis
        var y: Double
        while (x <= 2.0) {
            y = f(x)
            series.appendData(DataPoint(x, y), true, 400)
            x += 0.01
        }
        graphView.addSeries(series)
        graphView.visibility = View.VISIBLE
    }

    private fun clear() {
        // clear data
        table.text = ""
        graphView.removeAllSeries()
        graphView.visibility = View.INVISIBLE
    }

    // f''(x) = -12x^2 - 12x - 16
    private fun d2f(x: Double): Double {
        return -12*x.pow(2) - 12*x - 16
    }

    // f'(x) = -4x^3 - 6x^2 - 16x - 5
    private fun df(x: Double): Double {
        return -4*x.pow(3) - 6*x.pow(2) - 16*x - 5
    }

    // f(x) = -x^4 - 2x^3 - 8x^2 - 5x
    private fun f(x: Double): Double {
        return -1*x.pow(4) - 2*x.pow(3) - 8*x.pow(2) - 5*x
    }
}