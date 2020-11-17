package com.example.stegosaur

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle

class QRGen : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_gen)
        val actionBar = supportActionBar
        actionBar!!.title = "QRGen"
        actionBar.setDisplayHomeAsUpEnabled(true)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }
}