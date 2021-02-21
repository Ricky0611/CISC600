package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageButton
import android.widget.TextView
import androidx.fragment.app.Fragment
import com.example.ruiqi.cisc600.Equations.Companion.roundNumber
import kotlin.math.exp

/**
 * Fragment for M02 Hands-on & Drills.
 * Problem 2.18
 * To generate a table of velocities versus time from a given start time to a given end time at a given increment, the solution needs to:
 * 1. get valid inputs for start time, end time, and increment;
 * 2. loop through the time peroid, and calculate velocity with the given time based on the piecewise function;
 * 3. print all time stampes and their corresponding velocities in the format of "time, velocity".
 * Since float may cause representation errors in calculation, float numbers are rounded to the precision of the inputs.
 */
class M02Fragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_m02, container, false)
        button = view.findViewById(R.id.button)
        table = view.findViewById(R.id.table)
        startTimeView = view.findViewById(R.id.startTime)
        endTimeView = view.findViewById(R.id.endTime)
        increView = view.findViewById(R.id.increment)
        cancelButton = view.findViewById(R.id.cancelBtn)

        button.setOnClickListener {
            // clear data
            table.text = ""
            precision = 0
            // compute velocity
            val start = startTimeView.text.toString()
            val end = endTimeView.text.toString()
            val increment = increView.text.toString()
            if (checkInputs(start, end, increment)) {
                generateTable(start.toFloat(), end.toFloat(), increment.toFloat())
            }
        }
        // clear result area
        cancelButton.setOnClickListener {
            table.text = ""
            precision = 0
        }

        return view
    }

    private fun checkInputs(startTime: String, endTime: String, increments: String): Boolean{
        var result = true
        val text = StringBuilder()
        // check if inputs are valid numbers
        if (startTime.toFloatOrNull() == null){
            result = false
            text.append("Start time should be a valid number!\n")
        }
        if (endTime.toFloatOrNull() == null){
            result = false
            text.append("End time should be a valid number!\n")
        }
        if (increments.toFloatOrNull() == null){
            result = false
            text.append("Increment should be a valid number!\n")
        }
        if (result) { // check if the start time is earlier than the end time and the increment is positive
            val start = startTime.toFloat()
            val end = endTime.toFloat()
            val increment = increments.toFloat()
            if (start > end) {
                result = false
                text.append("Start time should be earlier than end time!\n")
            }
            if (increment <= 0) {
                result = false
                text.append("Increment should be positive!\n")
            }
            // get the largest precision of inputs
            if (startTime.contains(".")) {
                val tmp = startTime.substringAfter(".").length
                if (tmp > precision)
                    precision = tmp
            }
            if (endTime.contains(".")) {
                val tmp = endTime.substringAfter(".").length
                if (tmp > precision)
                    precision = tmp
            }
            if (increments.contains(".")) {
                val tmp = increments.substringAfter(".").length
                if (tmp > precision)
                    precision = tmp
            }
        }
        table.text = text.toString()
        return result
    }

    private fun generateTable(start: Float, end: Float, increment: Float) {
        val text = StringBuilder()
        var index = start
        while (index <= end) {
            index = roundNumber(precision, index)
            val result = getVelocity(index)
            text.append(index).append(", ").append(result).append("\n")
            index += increment
        }
        table.text = text.toString()
    }

    // Calculate velocity based on the given piecewise function
    private fun getVelocity(time: Float) : Float {
        return when {
            time in 0.0..10.0 -> { // 11t^2-5t
                (11 * time * time - 5 * time)
            }
            time in 10.0..20.0 -> { // 1100-5t
                (1100 - 5 * time)
            }
            time in 20.0..30.0 -> { // 50t+2(t-20)^2
                (50 * time + 2 * (time - 20) * (time - 20))
            }
            time > 30.0 -> { // 1520e^(-0.2(t-30))
                (1520 * exp(-0.2 * (time - 30))).toFloat()
            }
            else -> {
                0f
            }
        }
    }

    companion object {
        lateinit var button: Button
        lateinit var table: TextView
        lateinit var startTimeView: EditText
        lateinit var endTimeView: EditText
        lateinit var increView: EditText
        lateinit var cancelButton: ImageButton
        var precision = 0 // precision for inputs
    }
}