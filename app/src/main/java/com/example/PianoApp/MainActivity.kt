package com.example.PianoApp

import android.support.v7.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        this.initKeys()
    }
    private fun initKeys(){
        val r = resources
        val name = packageName
        var ref = findViewById<Button>(r.getIdentifier("b3", "id", name))
        ref.setOnClickListener {

        }
//        example
//        val cells = IntArray(81)
//        for (i in 0..80) {
//            if (i < 10)
//                cells[i] = r.getIdentifier("Squares0$i", "id", name)
//            else
//                cells[i] = r.getIdentifier("Squares$i", "id", name)
//        }
    }
}
