package com.example.ruiqi.cisc600

import kotlin.math.cos
import kotlin.math.pow

class Equations {
    companion object {
        // To avoid representation errors, round inputs based on the precision
        fun roundNumber(precision: Int, num: Float) : Float {
            return String.format("%.${precision}f", num).toFloat()
        }

        // To avoid representation errors, round result by twice the precision of the inputs.
        fun roundResult(precision: Int, res: Float) : Float {
            return String.format("%.${2* precision}f", res).toFloat()
        }

        // Get the percent tolerance according to Eq.(3.7)
        // n - number of significant figures
        fun getPercentTolerance(n: Int) : Double {
            return (0.5 * 10.toDouble().pow(2 - n)) * 100
        }

        // Get the true value by using mathematical functions and constants from package kotlin.math
        // x - the given parameter for cos function
        fun getTrueValueOfCos(x: Double) : Double {
            return cos(x)
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

        // Get the n-th term of the Maclaurin series expansion for cos(x)
        // (-1)^n * x^(2*n) / (2*n)!
        // x - the given parameter for cos function
        // n - the index of the term to be calculated, starting from 0
        fun getNthTermOfCos(x: Double, n: Int) : Double {
            return (-1).toDouble().pow(n) * x.pow(2*n) / getFactorial(2*n)
        }

        // Calculate the true percent relative error according to Eq.(3.3)
        // vt - the true value
        // ve - the estimate value
        fun getTruePercentRelativeError(vt: Double, ve: Double) : Double {
            return (vt-ve) / vt * 100
        }

        // Calculate the approximate percent relative error according to Eq.(3.5)
        // oldValue - the previous approximation
        // newValue - the current approximation
        fun getEstPercentRelativeError(oldValue: Double, newValue: Double) : Double {
            return (newValue-oldValue) / newValue * 100
        }
    }
}