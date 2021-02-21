package com.example.ruiqi.cisc600

import kotlin.math.atan
import kotlin.math.cos
import kotlin.math.ln
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
            return (0.5 * 10.toDouble().pow(2 - n))
        }

        // Get the true value by using mathematical functions and constants from package kotlin.math
        // x - the given parameter for cos function
        fun getTrueValueOfCos(x: Double) : Double {
            return cos(x)
        }

        // Get the true value by using mathematical functions and constants from package kotlin.math
        // x - the given parameter for arctan function
        fun getTrueValueOfArctangent(x: Double) : Double {
            return atan(x)
        }

        // Get the true value by using mathematical functions and constants from package kotlin.math
        // x - the given parameter for ln function
        fun getTrueValueOfLn(x: Double) : Double {
            return ln(x)
        }

        // Calculate factorial of a number
        // n - the given number to calculate n!
        fun getFactorial(n: Int) : Int {
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
        fun getNthMTermOfCos(x: Double, n: Int) : Double {
            return (-1).toDouble().pow(n) * x.pow(2*n) / getFactorial(2*n)
        }

        // Get the n-th term of the Maclaurin series expansion for arctan(x)
        // (-1)^n * x^(2*n+1) / (2*n+1)
        // x - the given parameter for arctan function
        // n - the index of the term to be calculated, starting from 0
        fun getNthMTermOfArctan(x: Double, n: Int) : Double {
            return (-1).toDouble().pow(n) * x.pow(2*n+1) / (2*n+1)
        }

        // Get the n-th derivative term of ln(x)
        // f(x) = ln(x), f(n)(x) = (-1)^(n-1) * x^(-n) * (n-1)! (n=1,2,…)
        // x - the given parameter for the function
        // n - the order of the term to be calculated, starting from 0
        fun getNthDerivativeTermOfLn(x: Double, n: Int) : Double {
            return if (n == 0) {
                ln(x)
            } else {
                (-1).toDouble().pow(n-1) * x.pow(-n) * getFactorial(n-1)
            }
        }

        // Get the n-th derivative term of ln(x) in String format
        // f(x) = ln(x), f(n)(x) = (-1)^(n-1) * x^(-n) * (n-1)! (n=1,2,…)
        // n - the order of the term to be calculated, starting from 0
        fun getNthDerivativeTermOfLnInString(n: Int) : String {
            return if (n == 0) {
                "ln(x)"
            } else if (n % 2 == 0) { // even
                val num = getFactorial(n-1).let {
                    if (it == 1)
                        ""
                    else
                        it.toString()
                }
                "-".plus(num).plus("x^(-$n)")
            } else { // odd
                getFactorial(n-1).let {
                    if (it == 1)
                        ""
                    else
                        it.toString()
                }.plus("x^(-$n)")
            }
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