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

    private val IMAGE_CODE = 1
    private val IMAGE_WRITE = 0
    companion object URIS {
        var IMAGE_URI: Uri? = null
        var QR_URI: Uri? = null
    }



    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.encode_image_activity)

        val actionBar = supportActionBar
        actionBar!!.title = "Device Images"
        actionBar.setDisplayHomeAsUpEnabled(true)

//        intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
//        intent.resolveActivity(packageManager)
//        startActivityForResult(intent, IMAGE_CODE)

        val button: Button = findViewById(R.id.begin)
        button.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            intent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, true)
            startActivityForResult(intent, 1)
        }



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

        if(requestCode == 1 && data != null){
            val mdata = data.clipData
            val img = mdata?.getItemAt(0)?.uri
            val qr = mdata?.getItemAt(1)?.uri

            val input1: InputStream? = img?.let { contentResolver.openInputStream(it) }
            val bitmap1: Bitmap = BitmapFactory.decodeStream(input1)

            val input2: InputStream? = img?.let { contentResolver.openInputStream(it) }
            val bitmap2: Bitmap = BitmapFactory.decodeStream(input2)

            val finalBitmap = Steganography.encode(bitmap2, bitmap1)

            val imageView = findViewById<ImageView>(R.id.selected_image)
            imageView.setImageBitmap(finalBitmap)



        }


//        if(requestCode == IMAGE_CODE && data != null) {
////            val imageView = findViewById<ImageView>(R.id.selected_image)
//            val data = data.data
//            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI).apply{
//                putExtra("text", data.toString())
//                startActivityForResult(intent, 2)
//            }
//            imageView.setImageURI(this.IMAGE_URI)

//            if(this.IMAGE_URI != null) {
//                val outIntent = Intent(Intent.ACTION_CREATE_DOCUMENT)
//                outIntent.addCategory(Intent.CATEGORY_OPENABLE)
//                outIntent.type = "image/png"
//                outIntent.putExtra(Intent.EXTRA_TITLE, IMAGE_URI.toString().substring(25))
//                startActivityForResult(outIntent, IMAGE_WRITE)
//            }
//        }
//        if(requestCode == 2 && data != null) {
//            val data = data.data
//
//
//            val imageView = findViewById<ImageView>(R.id.selected_image)
//            imageView.setImageURI(data)
//
//            val imageView2 = findViewById<ImageView>(R.id.selected_image2)
//            imageView2.setImageURI()
//        }
//
//        if(requestCode == IMAGE_WRITE){
//            try {
//                val input: InputStream? = data?.data?.let { contentResolver.openInputStream(it) }
//                val bitmap: Bitmap = BitmapFactory.decodeStream(input)
//                imageToStorage(this, bitmap)
//                Toast.makeText(this, "Image saved", Toast.LENGTH_SHORT).show()
//                input?.close()
//            } catch (e: Exception) {
//                e.printStackTrace()
//            }
//        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun setImageUri(uri: Uri){
        EncodeImage.IMAGE_URI = Uri.parse(uri.toString())
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