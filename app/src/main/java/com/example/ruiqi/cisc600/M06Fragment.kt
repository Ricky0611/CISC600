package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ruiqi.cisc600.Equations.Companion.getEstPercentRelativeError
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_m06.view.*
import kotlin.math.abs
import kotlin.math.absoluteValue
import kotlin.math.pow
import kotlin.math.sqrt

/**
 * Fragment for M05 Hands-on & Drills.
 * To find all real or complex roots of given polynomials:
 *     1. plot the functions to choose root guesses appropriately;
 *     2. apply any appropriate simplifications;
 *     3. if apply the Müller’s method:
 *         a) use 3 points (x0, x1, x2) to project a parabola,
 *         b) calculate differences based on the 3 points to get parameters a, b, and c for Eq.(7.17),
 *         c) apply the alternative quadratic formula to get the new estimate root (x3),
 *         d) repeat the above steps until the approximate percent relative error is less than the percent tolerance,
 *         e) if only real roots are located, choose the 2 original points that are nearest the new estimate root,
 *         f) if both real and complex roots are evaluated, repeat the process with x1, x2, and x3.
 *     4. if apply the Bairstow's method:
 *         a) provide initial guess for r and s in the quadratic factor x^2-r*x-s,
 *         b) get the coefficients of the second polynomial that is one order lower based on the given polynomial,
 *         c) get the coefficients of the third polynomial that is two order lower based on the given polynomial,
 *         d) get the differences for r and s based on the partial derivatives from the third polynomial,
 *         e) repeat the above steps until the approximate percent relative errors for r and s are less than the percent tolerance,
 *         f) if the quotient is
 *             i) a third-order polynomial or greater: Bairstow’s method would be applied to the quotient to evaluate new values for r and s, where the previous values of r and s can serve as the starting guesses for this application;
 *             ii) a quadratic: the remaining two roots could be evaluated directly with Eq.(7.39),
 *             iii) a first-order polynomial: the remaining single root can be evaluated simply as x = -s/r.
 *  The process and the final result will be printed out.
 * To get more accurate result, Double is used during calculation.
 * During display, all error percentage numbers are shown in Float to avoid possible representation errors.
 */
class M06Fragment : Fragment() {

    companion object {
        const val maxit = 20
        const val es = 0.0001 // percent tolerance
        lateinit var graphView: GraphView
        lateinit var table: TextView // result view
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_m06, container, false)

        graphView = view.findViewById(R.id.graphView)
        table = view.findViewById(R.id.table)
        view.button1.setOnClickListener {
            // clear data
            clear()
            // start solution
            solve1()
        }
        view.button2.setOnClickListener {
            // clear data
            clear()
            // start solution
            solve2()
        }
        view.button3.setOnClickListener {
            // clear data
            clear()
            // start solution
            solve3()
        }
        view.button4.setOnClickListener {
            // clear data
            clear()
            // start solution
            solve4()
        }
        view.button.setOnClickListener {
            // clear data
            clear()
            // start solution
            analyze()
        }

        return view
    }

    private fun clear() {
        // clear data
        table.text = ""
        graphView.removeAllSeries()
        graphView.visibility = View.INVISIBLE
    }

    private fun solve1() {
        table.append("f(x)=x^3-x^2+2x-2 (should have 3 roots)\n")
        // draw the graph for the given function
        draw(1)
        table.append("As we can see from the graph, the initial guess of root should be x = 0.\n")
        // find the root using Müller’s method
        table.append("Müller’s method (-2, 0, 2):\n")
        Muller(1, (-2).toDouble(), 0.toDouble(), 2.toDouble())
        table.append("Still 2 roots left. Choose 2 original points that are nearest the new root estimate.\n")
        table.append("Müller’s method (2, 3, 4):\n")
        Muller(1, 2.toDouble(), 3.toDouble(), 4.toDouble())
        table.append("The remaining 2 roots are complex roots. The detail process is in Muller_Octave_Code.\n")
        table.append("As we can see, a complex root -4.6614e-10+1.4142e+00i is found.\n")
        table.append("Since complex roots come in conjugate pairs, another complex root -4.6614e-10-1.4142e+00i is found.\n")
    }

    private fun solve2() {
        table.append("f(x)=2x^4+6x^2+8 (should have 4 roots)\n")
        // draw the graph for the given function
        draw(2)
        table.append("As we can see from the graph, the initial guess of root should be x = 0.\n")
        // find the root using Müller’s method
        table.append("Müller’s method (-2, 0, 2):\n")
        Muller(2, (-2).toDouble(), 0.toDouble(), 2.toDouble())
        table.append("All 4 roots are complex roots. The detail process is in Muller_Octave_Code.\n")
        table.append("As we can see, 2 complex root 0.5000+1.3229i and -0.5000+1.3229i are found.\n")
        table.append("Since complex roots come in conjugate pairs, another 2 complex root 0.5000-1.3229i and -0.5000-1.3229i are found.\n")
    }

    private fun solve3() {
        table.append("f(x)=-2+6.2x-4x^2+0.7x^3 (should have 3 roots)\n")
        // draw the graph for the given function
        draw(3)
        table.append("Let's start with the initial guess of r, s that r = s = -1.\n")
        // find the root using Bairstow’s method
        table.append("Bairstow’s method:\n")
        val a = arrayOf(-2.0, 6.2, -4.0, 0.7)
        Bairstow(a, 3, -1.0, -1.0)
        table.append("\nAll 3 roots are real roots.\n")
    }

    private fun solve4() {
        table.append("f(x)=x^4-2x^3+6x^2-2x+5 (should have 4 roots)\n")
        // draw the graph for the given function
        draw(4)
        table.append("Let's start with the initial guess of r, s that r = s = -1.\n")
        // find the root using Bairstow’s method
        table.append("Bairstow’s method:\n")
        val a = arrayOf(5.0, -2.0, 6.0, -2.0, 1.0)
        Bairstow(a, 4, -1.0, -1.0)
        table.append("\nAll 4 roots are complex roots.\n")
    }

    private fun draw(type: Int) {
        // set manual X bounds
        graphView.viewport.apply {
            isXAxisBoundsManual = true
            setMinX(-10.0)
            setMaxX(10.0)
        }
        // generate a series of data points that covers [-10.0, 10.0]
        val series = LineGraphSeries<DataPoint>()
        var x = -10.0 // starting point on x axis
        var y: Double
        while (x <= 10.0) {
            y = calculateFun(type, x)
            series.appendData(DataPoint(x, y), true, 200)
            x += 0.1
        }
        graphView.addSeries(series)
        graphView.visibility = View.VISIBLE
    }

    private fun calculateFun(equationType: Int, x: Double) : Double {
        return when (equationType) {
            4 -> x.pow(4) - 2*x.pow(3) + 6*x.pow(2) - 2*x + 5 // f(x)=x^4-2x^3+6x^2-2x+5
            3 -> -2 + 6.2*x - 4*x.pow(2) + 0.7*x.pow(3) // f(x)=-2+6.2x-4x^2+0.7x^3
            2 -> 2*x.pow(4) + 6*x.pow(2) + 8 // f(x)=2x^4+6x^2+8
            else -> x.pow(3) - x.pow(2) + 2*x - 2 // f(x)=x^3-x^2+2x-2
        }
    }

    private fun Muller(type: Int, para1: Double, para2: Double, para3: Double) {
        printMullerRow("Iteration", "x2", "f(x2)=c", "a", "b", "x3", "ea(%)")
        // init 3 points for a parabola
        var x0 = para1
        var x1 = para2
        var x2 = para3
        // next estimate x2
        var xr: Double
        // define approximate percent relative error
        var ea: Double
        // define differences
        var h0: Double
        var h1: Double
        var d0: Double
        var d1: Double
        var dxr: Double
        // define coefficients for the parabolic equation
        var a: Double
        var b: Double
        var c: Double
        // define temporary variables in Eq.(7.27a)
        var det: Double
        var rad: Double
        var den: Double
        // start iteration to find roots
        var iter = 0
        do {
            iter++
            c = calculateFun(type, x2)
            h0 = x1 - x0
            h1 = x2 - x1
            d0 = (calculateFun(type, x1) - calculateFun(type, x0)) / h0
            d1 = (c - calculateFun(type, x1)) / h1
            a = (d1 - d0) / (h1 + h0)
            b = a * h1 + d1
            det = b*b - 4*a*c
            if (det >= 0) {
                rad = sqrt(det)
                den = if (abs(b + rad) >= abs(b - rad))
                    b + rad
                else
                    b - rad
                dxr = -2 * c / den
                xr = x2 + dxr
                ea = getEstPercentRelativeError(x2, xr)
                printMullerRow(
                    iter.toString(),
                    x2.toFloat().toString(),
                    c.toFloat().toString(),
                    a.toFloat().toString(),
                    b.toFloat().toString(),
                    xr.toFloat().toString(),
                    ea.toString()
                )
                if (ea < es) {
                    table.append("Found approximate root $xr\n")
                    break
                }
                x0 = x1
                x1 = x2
                x2 = xr
            } else {
                rad = sqrt(det.absoluteValue)
                printMullerRow(
                    iter.toString(),
                    x2.toFloat().toString(),
                    c.toFloat().toString(),
                    a.toFloat().toString(),
                    b.toFloat().toString(),
                    "",
                    ""
                )
                table.append("The calculation involves imaginary numbers, which cannot handled by Kotlin.\n")
                break
            }
        } while (iter <= maxit)
        if (iter > maxit) {
            table.append("No root find during the $maxit iteration.\n")
        }
    }

    private fun Bairstow(array: Array<Double>, order: Int, rr: Double, ss: Double) {
        var r = rr
        var s = ss
        var n = order
        var a = array
        var b = arrayOfNulls<Double>(n+1)
        var c = arrayOfNulls<Double>(n+1)
        var iter = 0
        var ea1 = 1.0
        var ea2 = 1.0
        do {
            if (n < 3)
                break
            do {
                iter++
                table.append("Iteration $iter:\n")
                table.append("a = ${a.contentDeepToString()}\n")
                b[n] = a[n]
                b[n-1] = a[n-1] + r * b[n]!!
                c[n] = b[n]
                c[n-1] = b[n-1]!! + r * c[n]!!
                for (i in n-2 downTo 0 step 1) {
                    b[i] = a[i] + r * b[i+1]!! + s * b[i+2]!!
                    c[i] = b[i]!! + r * c[i+1]!! + s * c[i+2]!!
                }
                table.append("b = ${b.contentToString()}\nc = ${c.contentToString()}\n")
                var det = c[2]!! * c[2]!! - c[3]!! * c[1]!!
                if (det != 0.0) {
                    var dr = (-b[1]!! * c[2]!! + b[0]!! * c[3]!!) / det
                    var ds = (-b[0]!! * c[2]!! + b[1]!! * c[1]!!) / det
                    r += dr
                    s += ds
                    if (r != 0.0) {
                        ea1 = (dr/r).absoluteValue * 100
                    }
                    if (s != 0.0) {
                        ea2 = (ds/s).absoluteValue * 100
                    }
                } else {
                    r += 1
                    s += 1
                    iter = 0
                }
                table.append("r = $r, s = $s, ea1 = $ea1, ea2 = $ea2\n")
                if (ea1 < es && ea2 < es) {
                    break
                }
            } while (iter <= maxit)
            quadRoot(r, s)
            n -= 2
            for (i in 0..n) {
                a[i] = b[i+2]!!
            }
        } while (iter <= maxit)
        if (iter <= maxit) {
            if (n == 2) {
                r = - a[1] / a[2]
                s = - a[0] / a[2]
                quadRoot(r, s)
            } else {
                var x = - a[0] / a[1]
                table.append("\nFound approximate root ${x.toFloat()}.\n")
            }
        } else {
            table.append("\nNo root find during the $maxit iteration.\n")
        }
    }

    private fun quadRoot(r: Double, s: Double) {
        var r1: Double
        var i1 = 0.0
        var r2: Double
        var i2 = 0.0
        var dis = r * r + 4 * s
        if (dis > 0) {
            r1 = (r + sqrt(dis)) / 2
            r2 = (r - sqrt(dis)) / 2
            table.append("\nFound approximate roots ${r1.toFloat()} and ${r2.toFloat()}.\n")
        } else {
            r1 = r / 2
            r2 = r1
            i1 = sqrt(dis.absoluteValue) / 2
            i2 = -i1
            table.append("\nFound approximate roots ${r1.toFloat()}+(${i1.toFloat()})i and ${r2.toFloat()}+(${i2.toFloat()})i.\n")
        }
    }

    private fun printMullerRow(iter: String, x2: String, c: String, a: String, b: String, x3: String, ea: String) {
        val text = StringBuilder().apply {
            append(iter.padStart(5, ' '))
            append(" ")
            append(x2.padStart(10, ' '))
            append(" ")
            append(c.padStart(10, ' '))
            append(" ")
            append(a.padStart(10, ' '))
            append(" ")
            append(b.padStart(10, ' '))
            append(" ")
            append(x3.padStart(10, ' '))
            append(" ")
            append(ea.padStart(15, ' '))
            append("\n")
        }
        table.append(text.toString())
    }

    private fun analyze() {
        graphView.visibility = View.GONE
        val analysis = StringBuilder("\n").apply {
            append("The Müller’s method is more easy to implement, but is not friendly for programming languages that cannot calculate imaginary numbers.\n")
            append("The Bairstow’s method, though its implementation is more complex, can find all types of roots, which is friendly to all programming languages.")
        }.toString()
        table.append(analysis)
    }
}