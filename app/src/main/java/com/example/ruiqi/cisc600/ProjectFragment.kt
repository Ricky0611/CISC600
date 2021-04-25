package com.example.ruiqi.cisc600

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_project.view.*
import java.util.*
import kotlin.math.*

/**
 * Fragment for Project (Problem 8.2).
 * Solved with all bracketing methods and open methods mentioned in Chapter 5 and 6.
 * Also document their runtime for performance comparison.
 */
class ProjectFragment : Fragment() {

    companion object {
        const val TAG = "Timestamp"
        lateinit var graphView: GraphView
        const val xaf = 0.9
        const val es = 0.001 // percent tolerance
    }

    private lateinit var projectView: View

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        projectView = inflater.inflate(R.layout.fragment_project, container, false)

        // Graphical method
        graphView = projectView.graphView
        drawGraph()

        // Bisection method
        bisection()

        // False-Position method
        falsePosition()

        // Fixed-Position
        fixedPosition(0.1)
        fixedPosition(0.2)
        fixedPosition(0.3)
        fixedPosition(0.4)
        fixedPosition(0.5)

        // Newton-Raphson method
        newtonRaphson()

        // Secant method
        secant()

        // Brent's method
        brent()

        return projectView
    }

    private fun brent() {
        Log.i(TAG, "Brent starts at ${Date().time}")
        val eps = 2.22044604925031E-16 // machine epsilon
        var tol = es // tolerance
        // define search interval (a, b, c)
        var a = 0.1 // assign lower bound
        var b = 0.5 // assign upper bound
        var c = a
        var fa = f(a) // function value at a
        var fb = f(b) // function value at b
        var fc = fa // function value at c
        var d = b - c
        var e = d
        var iter = 0
        printBrentRow("iteration", "a", "b", "c", "d", "e", "fa", "fb", "fc")
        while (true) {
            printBrentRow(iter.toString(), a.toString(), b.toString(), c.toString(), d.toString(), e.toString(), fa.toString(), fb.toString(), fc.toString())
            if (fb == 0.0) { // find root
                break
            }
            if (fa.sign == fb.sign) {
                a = c
                fa = fc
                d = b - c
                e = d
            }
            if (fa.absoluteValue < fb.absoluteValue) {
                c = b
                b = a
                a = c
                fc = fb
                fb = fa
                fa = fc
            }
            printBrentRow(iter.toString(), a.toString(), b.toString(), c.toString(), d.toString(), e.toString(), fa.toString(), fb.toString(), fc.toString())
            iter++
            val m = (a - b) / 2
            tol = 2 * eps * max(b.absoluteValue, 1.0)
            if (m.absoluteValue <= tol || fb == 0.0) { // find (approximate) root
                break
            }
            if (e.absoluteValue >= tol && fc.absoluteValue > fb.absoluteValue) { // open methods
                val s = fb / fc
                var p: Double
                var q: Double
                if (a == c) { // Secant
                    p = 2 * m * s
                    q = 1 - s
                } else { // Inverse quadratic interpolation
                    q = fc / fa
                    val r = fb / fa
                    p = s * (2 * m * q * (q - r) - (b - c) * (r - 1))
                    q = (q - 1) * (r - 1) * (s - 1)
                }
                if (p > 0) {
                    q = -q
                } else {
                    p = -p
                }
                if (2 * p < 3 * m * q - (tol * q).absoluteValue && p < (0.5 * e * q).absoluteValue) {
                    e = d
                    d = p / q
                } else {
                    d = m
                    e = m
                }
            } else { // Bisection
                d = m
                e = m
            }
            c = b
            fc = fb
            if (d.absoluteValue > tol) {
                b += d
            } else {
                b -= (b - a).sign * tol
            }
            fb = f(b)
        }
        Log.i(TAG, "Brent ends at ${Date().time}")
        projectView.brentText.append("\nf($b)=${f(b)}")
    }

    private fun printBrentRow(iter: String, a: String, b: String, c: String, d: String, e: String, fa: String, fb: String, fc: String) {
        val text = StringBuilder().apply {
            append(iter.padStart(5, ' '))
            append(", ")
            append(a.padStart(15, ' '))
            append(", ")
            append(b.padStart(15, ' '))
            append(", ")
            append(c.padStart(15, ' '))
            append(", ")
            append(d.padStart(15, ' '))
            append(", ")
            append(e.padStart(15, ' '))
            append(", ")
            append(fa.padStart(15, ' '))
            append(", ")
            append(fb.padStart(15, ' '))
            append(", ")
            append(fc.padStart(15, ' '))
            append("\n")
        }
        projectView.brentText.append(text.toString())
    }

    private fun secant() {
        Log.i(TAG, "Secant starts at ${Date().time}")
        printBracketRow(projectView.secantText, "Iteration", "x0", "x1", "x2", "ea(%)")
        var iter = 0 // iteration
        var x0 = 0.1 // first initial estimate point
        var x1 = 0.5 // second initial estimate point
        var x2: Double // next estimate point
        var ea: Double // approximate percent relative error
        do {
            iter++
            x2 = x1 - f(x1) * (x0 - x1) / (f(x0) - f(x1))
            if (x2 >= 0.0) {
                ea = Equations.getEstPercentRelativeError(x1, x2)
                printBracketRow(projectView.secantText, iter.toString(), x0.toString(), x1.toString(), x2.toString(), ea.toFloat().toString())
                x0 = x1
                x1 = x2
            } else {
                break
            }
        } while (ea >= es)
        Log.i(TAG, "Secant ends at ${Date().time}")
        projectView.secantText.append("\nf($x1)=${f(x1)}")
    }

    private fun newtonRaphson() {
        Log.i(TAG, "Newton-Raphson starts at ${Date().time}")
        printOpenRow(projectView.newRapText, "Iteration", "x", "f(x)", "ea(%)")
        var iter = 0 // iteration
        var x = 0.1 // start point
        var xold: Double
        var ea = 1.0 // approximate percent relative error
        var fx = f(x) // function value at x
        do {
            xold = x
            x = xold - f(xold)/fd(xold)
            iter++
            if (x >= 0.0) {
                ea= Equations.getEstPercentRelativeError(xold, x)
                fx = f(x)
                printOpenRow(projectView.newRapText, iter.toString(), x.toString(), fx.toString(), ea.toFloat().toString())
            } else {
                break
            }
        } while (ea >= es)
        Log.i(TAG, "Newton-Raphson ends at ${Date().time}")
        projectView.newRapText.append("\nf($x)=$fx")
    }

    private fun fixedPosition(init: Double) {
        Log.i(TAG, "Fixed-Position starts at ${Date().time}")
        var iter = 0 // iteration
        var x = init // initial guess
        var xold: Double
        var ea = Double.NaN // approximate percent relative error
        var fx = f(x) // function value at x
        var isConvergent = gd(x).absoluteValue < 1
        if (isConvergent) {
            printOpenRow(projectView.fixPtText, "Iteration", "x", "f(x)", "ea(%)")
        }
        while (isConvergent && ea >= es) {
            printOpenRow(projectView.fixPtText, iter.toString(), x.toString(), fx.toString(), ea.toFloat().toString())
            isConvergent = gd(x).absoluteValue < 1
            if (isConvergent) {
                xold = x
                x = g(xold)
                iter++
                if (x >= 0.0) {
                    ea = Equations.getEstPercentRelativeError(xold, x)
                    fx = f(x)
                } else {
                    break
                }
            } else {
                break
            }
        }
        Log.i(TAG, "Fixed-Position ends at ${Date().time}")
        if (isConvergent) {
            projectView.fixPtText.append("\nf($x)=$fx")
        } else {
            projectView.fixPtText.append("Fixed-Position method is divergent at x = $x\n")
        }
    }

    private fun printOpenRow(view: TextView, iter: String, x: String, result: String, ea: String) {
        val text = StringBuilder().apply {
            append(iter.padStart(5, ' '))
            append(", ")
            append(x.padStart(15, ' '))
            append(", ")
            append(result.padStart(15, ' '))
            append(", ")
            append(ea.padStart(15, ' '))
            append("\n")
        }
        view.append(text.toString())
    }

    private fun falsePosition() {
        Log.i(TAG, "False-Position starts at ${Date().time}")
        printBracketRow(projectView.falsePosText, "Iteration", "xl", "xu", "xr", "ea(%)")
        var iter = 0 // iteration
        var xl = 0.1 // lower bound
        var xu = 0.5 // upper bound
        var xr: Double // midpoint
        var ea: Double // approximate percent relative error
        var fl = f(xl) // function value at xl
        var fu = f(xu)  // function value at xu
        var fr: Double  // function value at xr
        var test: Double
        var iu = 0 // index of upper bound
        var il = 0 // index of lower bound
        do {
            iter++
            xr = xu - (fu*(xl - xu)/(fl - fu)) // Eq.(5.7)
            fr = f(xr)
            ea = Equations.getEstPercentRelativeErrorForBracket(xl, xu)
            printBracketRow(projectView.falsePosText, iter.toString(), xl.toString(), xu.toString(), xr.toString(), ea.toFloat().toString())
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
        } while (ea.absoluteValue >= es)
        Log.i(TAG, "False-Position ends at ${Date().time}")
        projectView.falsePosText.append("\nf($xr)=$fr")
    }

    private fun bisection() {
        Log.i(TAG, "Bisection starts at ${Date().time}")
        printBracketRow(projectView.bisectionText, "Iteration", "xl", "xu", "xr", "ea(%)")
        var iter = 0 // iteration
        var xl = 0.1 // lower bound
        var xu = 0.5 // upper bound
        var xr: Double // midpoint
        var ea: Double // approximate percent relative error
        var fl = f(xl) // function value at xl
        var fr: Double // function value at xr
        var test: Double
        do {
            iter++
            xr = (xl + xu) / 2
            fr = f(xr)
            ea = Equations.getEstPercentRelativeErrorForBracket(xl, xu)
            printBracketRow(projectView.bisectionText, iter.toString(), xl.toString(), xu.toString(), xr.toString(), ea.toFloat().toString())
            test = fl * fr
            if (test < 0) {
                xu = xr
            } else if (test > 0) {
                xl = xr
                fl = fr
            } else { // xr is the root
                ea = 0.0
            }
        } while (ea.absoluteValue >= es)
        Log.i(TAG, "Bisection ends at ${Date().time}")
        projectView.bisectionText.append("\nf($xr)=$fr")
    }

    private fun printBracketRow(view: TextView, iter: String, xl: String, xu: String, xr: String, ea: String) {
        val text = StringBuilder().apply {
            append(iter.padStart(5, ' '))
            append(", ")
            append(xl.padStart(15, ' '))
            append(", ")
            append(xu.padStart(15, ' '))
            append(", ")
            append(xr.padStart(15, ' '))
            append(", ")
            append(ea.padStart(15, ' '))
            append("\n")
        }
        view.append(text.toString())
    }

    private fun drawGraph() {
        Log.i(TAG, "Graph starts at ${Date().time}")
        // set manual X bounds
        graphView.viewport.apply {
            isXAxisBoundsManual = true
            setMinX(0.0)
            setMaxX(2.0)
        }
        // generate a series of data points that covers [0.0, 2.0]
        val series = LineGraphSeries<DataPoint>()
        var x = 0.0 // starting point on x axis
        var y: Double
        while (x <= 2.0) {
            y = f(x)
            series.appendData(DataPoint(x, y), true, 200)
            x += 0.1
        }
        graphView.addSeries(series)
        Log.i(TAG, "Graph ends at ${Date().time}")
        projectView.graphText.text = "As we can see, the root is located inside x = (0, 0.5). And this can be used as the initial guess for following methods."
    }

    // Derivative of g(x)
    private fun gd(x: Double) : Double {
        return (1 - xaf)/((1 - xaf) * x + 1) - 1/x - ((1 - xaf) * x.pow(2) - (1 - xaf) * 2 * x - 1)/((1 - xaf) * x.pow(2) + x).pow(2) + 1
    }

    // f(x) = 0 -> g(x) = x by adding x to both side
    private fun g(x: Double) : Double {
        return f(x) + x
    }

    // Derivative of f(x)
    private fun fd(x: Double) : Double {
        return (1 - xaf) / (1 + x * (1 - xaf)) - 1/x + (x.pow(2) * (1 - xaf) + 2 * x * (1 - xaf) + 1)/((1 - xaf).pow(2) * x.pow(4) + 2 * (1 - xaf) * x.pow(3) + x.pow(2))
    }

    // x cannot be 0, otherwise divide by zero error will happen
    private fun f(x: Double) : Double {
        return ln((1 + x * (1 - xaf)) / (x * (1 - xaf))) - (x + 1) / (x * (1 + x * (1 - xaf)))
    }
}