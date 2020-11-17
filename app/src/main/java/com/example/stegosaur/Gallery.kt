package com.example.stegosaur

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView


class Gallery : AppCompatActivity() {

    private val IMAGE_CODE = 1
    private var IMAGE_URI: Uri? = null
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.gallery)

        val actionBar = supportActionBar
        actionBar!!.title = "Device Images"
        actionBar.setDisplayHomeAsUpEnabled(true)

        intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.resolveActivity(packageManager)
        startActivityForResult(intent, IMAGE_CODE)

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        val imageView = findViewById<ImageView>(R.id.gallery_view)
        IMAGE_URI = data?.data
        imageView.setImageURI(IMAGE_URI)
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

}