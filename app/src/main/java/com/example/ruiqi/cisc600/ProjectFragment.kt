package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.jjoe64.graphview.GraphView
import com.jjoe64.graphview.series.DataPoint
import com.jjoe64.graphview.series.LineGraphSeries
import kotlinx.android.synthetic.main.fragment_project.view.*
import kotlin.math.ln

/**
 * Fragment for Project (Problem 8.2).
 */
class ProjectFragment : Fragment() {

    companion object {
        lateinit var graphView: GraphView
        const val xaf = 0.9
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

        return projectView
    }

    private fun drawGraph() {
        // set manual X bounds
        graphView.viewport.apply {
            isXAxisBoundsManual = true
            setMinX(0.0)
            setMaxX(5.0)
        }
        // generate a series of data points that covers [0.0, 5.0]
        val series = LineGraphSeries<DataPoint>()
        var x = 0.0 // starting point on x axis
        var y: Double
        while (x <= 5.0) {
            y = calculateFun(x)
            series.appendData(DataPoint(x, y), true, 500)
            x += 0.1
        }
        graphView.addSeries(series)
        projectView.graphText.text = "As we can see, the root is located inside x = (0, 1). And this can be used as the initial guess for following methods."
    }

    private fun calculateFun(x: Double) : Double {
        return ln((1 + x * (1 - xaf)) / (x * (1 - xaf))) - (x + 1) / (x * (1 + x * (1 - xaf)))
    }
}