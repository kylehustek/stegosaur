package com.example.stegosaur

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.ImageDecoder
import android.graphics.drawable.BitmapDrawable
import android.media.MediaExtractor
import android.media.MediaParser
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.ParcelFileDescriptor
import android.provider.MediaStore
import android.renderscript.ScriptGroup
import android.widget.ArrayAdapter
import android.widget.GridView
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.content.contentValuesOf
import androidx.core.net.toFile
import java.io.FileDescriptor
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.InputStream

class EncodeImage : AppCompatActivity() {

    private val IMAGE_CODE = 1
    private val IMAGE_WRITE = 0
    private var IMAGE_URI: Uri? = null

    

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encode_image_activity)

        val actionBar = supportActionBar
        actionBar!!.title = "Device Images"
        actionBar.setDisplayHomeAsUpEnabled(true)

        intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
        intent.resolveActivity(packageManager)
        startActivityForResult(intent, IMAGE_CODE)
//        val image = findViewById<ImageView>(R.id.selected_image)
//        val resolver = applicationContext.contentResolver
//        val pfd: ParcelFileDescriptor? = IMAGE_URI?.let { contentResolver.openFileDescriptor(it, "r") }
//        val fd: FileInputStream = FileInputStream(pfd?.fileDescriptor)
//        val bitmap: Bitmap = BitmapFactory.decodeStream(fd)
//        if (bitmap != null) {
//            imageToStorage(this, bitmap)
//            Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()
//        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if(requestCode == IMAGE_CODE) {
            val imageView = findViewById<ImageView>(R.id.selected_image)
            val data = data?.data
            this.IMAGE_URI = data
            imageView.setImageURI(this.IMAGE_URI)

            if(this.IMAGE_URI != null) {
                val outIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
                outIntent.addCategory(Intent.CATEGORY_OPENABLE)
                outIntent.type = "image/png"
                outIntent.putExtra(Intent.EXTRA_TITLE, IMAGE_URI.toString().substring(25))
                startActivityForResult(outIntent, IMAGE_WRITE)
            }
        }

         if(requestCode == IMAGE_WRITE){
           try {
               val input: InputStream? = data?.data?.let { contentResolver.openInputStream(it) }
               val bitmap: Bitmap = BitmapFactory.decodeStream(input)
               imageToStorage(this, bitmap)
               Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()
               input?.close()
           } catch (e: Exception) {
               e.printStackTrace()
           }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    private fun imageToStorage(context: Context, bitmap: Bitmap){
        val contValues = ContentValues()
        contValues.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
        contValues.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/Stegosaur")
        contValues.put(MediaStore.Images.Media.IS_PENDING, true)
        val uri: Uri? = context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, contValues)
        if(uri != null){
            val outputStream = context.contentResolver.openOutputStream(uri)
            if(outputStream != null){
                try {
                    bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
                    outputStream.close()
                } catch (e: Exception){
                    Toast.makeText(context, "File Write Error", Toast.LENGTH_SHORT).show()
                }
            }
            contValues.put(MediaStore.Images.Media.IS_PENDING, false)
            context.contentResolver.update(uri, contValues, null, null)
        }
    }
}