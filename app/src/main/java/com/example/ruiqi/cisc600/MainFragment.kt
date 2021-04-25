package com.example.ruiqi.cisc600

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.commit
import androidx.fragment.app.replace
import kotlinx.android.synthetic.main.fragment_main.view.*

/**
 * The default fragment for MainActivity.
 * Provide navigation to selected Hands-on & Drills.
 */
class MainFragment : Fragment() {

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_main, container, false)

        view.m02.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<M02Fragment>(R.id.containerView)
                addToBackStack(null)
            }
        }

        view.m03.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<M03Fragment>(R.id.containerView)
                addToBackStack(null)
            }
        }

        view.m04_1.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<M041Fragment>(R.id.containerView)
                addToBackStack(null)
            }
        }

        view.m04_2.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<M042Fragment>(R.id.containerView)
                addToBackStack(null)
            }
        }

        view.m05.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<M05Fragment>(R.id.containerView)
                addToBackStack(null)
            }
        }

        view.m06.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<M06Fragment>(R.id.containerView)
                addToBackStack(null)
            }
        }

        view.project.setOnClickListener {
            activity?.supportFragmentManager?.commit {
                setReorderingAllowed(true)
                replace<ProjectFragment>(R.id.containerView)
                addToBackStack(null)
            }
        }

        return view
    }

}