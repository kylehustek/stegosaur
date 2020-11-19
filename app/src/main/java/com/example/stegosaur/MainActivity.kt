package com.example.stegosaur

import android.app.Notification
import android.content.Intent
import android.graphics.Bitmap
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.MediaStore.Images.*
import android.view.Window

import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        this.supportActionBar?.hide()

        val logo:ImageView = findViewById(R.id.stego_logo)
        logo.setImageResource(R.drawable.ic_stegosaurus)

        val beginEncode: Button = findViewById(R.id.to_hide_image)
        beginEncode.setOnClickListener{
            val intent = Intent(this, EncodeImage::class.java)
            startActivity(intent)
        }

        val genQR: Button = findViewById(R.id.qr_gen)
        genQR.setOnClickListener{
            val intent = Intent(this, QRGen::class.java)
            startActivity(intent)
        }

        val beginDecode: Button = findViewById(R.id.begin_decode)
        beginDecode.setOnClickListener{decodeImage() }

    }


    private fun decodeImage() {
        Toast.makeText(this, "Implement Decode", Toast.LENGTH_SHORT).show()
    }


}






