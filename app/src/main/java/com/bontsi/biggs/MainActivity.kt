package com.bontsi.biggs

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val tv = TextView(this)
        tv.text = "BIGGS IS LIVE! 🔥\n\nGaborone's Siri\n\nBuilt by Bontsi\n\nNo crash!"
        tv.textSize = 22f
        tv.setPadding(80, 400, 80, 80)
        setContentView(tv)
    }
}
