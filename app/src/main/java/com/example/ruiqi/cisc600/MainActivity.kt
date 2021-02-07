package com.example.ruiqi.cisc600

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.commit

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
    }

    override fun onBackPressed() {
        supportFragmentManager.findFragmentById(R.id.containerView)?.let { fragment ->
            supportFragmentManager.commit {
                remove(fragment)
            }
        }
        super.onBackPressed()
    }
}
