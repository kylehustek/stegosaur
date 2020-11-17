package com.example.stegosaur

import android.graphics.Bitmap
import android.media.Image
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.provider.MediaStore.Images
import android.provider.MediaStore.Images.*

import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val gallery: Button = findViewById(R.id.gallery_view)
        gallery.setOnClickListener{ seeGallery() }
//
//        val selectImage: Button = findViewById(R.id.select_label)
//        saveButton.setOnClickListener{ selectImage() }

    }

    fun seeGallery(){
        setContentView(R.layout.gallery)
    }

//    private fun selectImage(){
//
//        var mediaList: LiveData<List<Picture>>
//
//        val images_uri = Media.EXTERNAL_CONTENT_URI
//        val proj = arrayOf(Media._ID, Media.DISPLAY_NAME, Media.DATE_ADDED)
//        val selection = null
//        val selectionArgs = null
//        val sortOrder = "${Media.DATE_ADDED} DESC"
//
//        val images_cursor = contentResolver.query(images_uri, proj, selection, selectionArgs, sortOrder)
//
//        if (images_cursor != null) {
//            while (images_cursor.moveToNext()){
//                val id = images_cursor.getLong(images_cursor.getColumnIndexOrThrow(MediaStore.Images.Media._ID))
//                val name = images_cursor.getString(images_cursor.getColumnIndexOrThrow(MediaStore.Images.Media.DISPLAY_NAME))
//                val pic = Picture(id, name)
//
//            }
//        }
//
//
//    }

    private fun saveFile() {
        //Toast.makeText(this, "button clicked", Toast.LENGTH_SHORT).show()
        val randomInt = (1..6).random()
        val resultText: TextView = findViewById(R.id.result_text)
        resultText.text = randomInt.toString()
    }
}




