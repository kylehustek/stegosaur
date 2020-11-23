package com.example.stegosaur

import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import java.io.InputStream

class EncodeImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encode_image_activity)

        val actionBar = supportActionBar
        actionBar!!.title = "Device Images"
        actionBar.setDisplayHomeAsUpEnabled(true)

        val button: Button = findViewById(R.id.begin)
        button.setOnClickListener{
            val intent = Intent(Intent.ACTION_GET_CONTENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(Intent.createChooser(intent, "Add images"), 1);
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == 1 && data != null){
            val mdata = data.clipData
            val img = mdata?.getItemAt(0)?.uri
            val qr = mdata?.getItemAt(1)?.uri

            val input1: InputStream? = img?.let { contentResolver.openInputStream(it) }
            val bitmap1: Bitmap = BitmapFactory.decodeStream(input1)

            val input2: InputStream? = qr?.let { contentResolver.openInputStream(it) }
            val bitmap2: Bitmap = BitmapFactory.decodeStream(input2)

            val imageView1 = findViewById<ImageView>(R.id.selected_image1)
            val imageView2 = findViewById<ImageView>(R.id.selected_image2)
            val imageView3 = findViewById<ImageView>(R.id.fin)
            imageView1.setImageBitmap(bitmap1)
            imageView2.setImageBitmap(bitmap2)


            val button2: Button = findViewById(R.id.encode)
            button2.setOnClickListener {
                val finalBitmap = Steganography.encode(bitmap1, bitmap2)
                imageView3.setImageBitmap(finalBitmap)
                imageToStorage(this, finalBitmap)
                Toast.makeText(this, "Image saved!", Toast.LENGTH_SHORT).show()
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