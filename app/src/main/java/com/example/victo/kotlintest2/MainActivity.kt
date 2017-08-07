package com.example.victo.kotlintest2


import android.app.Activity
import android.os.Bundle


class MainActivity : Activity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val v = findViewById(R.id.view) as CustomView
        v.maxValue=1024
        v.proportion = 0.8f
    }
}
