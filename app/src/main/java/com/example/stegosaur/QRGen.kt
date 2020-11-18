package com.example.stegosaur

import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.google.zxing.BarcodeFormat
import com.google.zxing.MultiFormatWriter
import com.google.zxing.WriterException




class QRGen : AppCompatActivity() {
    private var editText: EditText? = null
    private var button: Button? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_q_r_gen)
        val actionBar = supportActionBar
        actionBar!!.title = "QR Generator"
        actionBar.setDisplayHomeAsUpEnabled(true)

        editText = findViewById(R.id.qr_text)
        button = findViewById(R.id.qr_create_button)

        (this.button as Button).setOnClickListener{
            val qrText = (this.editText as EditText).text.toString()

            if(qrText == ""){
                Toast.makeText(this, "Error, no text input", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val bitmap: Bitmap = generateQRCode((this.editText as EditText).text.toString())
            imageToStorage(this, bitmap)
            Toast.makeText(this, "QR Code saved to Pictures/Stegosaur", Toast.LENGTH_LONG).show()
        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    //Method taken and modified from 3rd party zxing library documentation
    private fun generateQRCode(text: String): Bitmap {
        val width = 500
        val height = 500
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val codeWriter = MultiFormatWriter()
        try {
            val bitMatrix = codeWriter.encode(text.toString(), BarcodeFormat.QR_CODE, width, height)
            for (x in 0 until width) {
                for (y in 0 until height) {
                    bitmap.setPixel(x, y, if (bitMatrix[x, y]) Color.BLACK else Color.WHITE)
                }
            }
        } catch (e: WriterException) {
            Toast.makeText(this, "Error generating QR", Toast.LENGTH_SHORT).show()
        }
        return bitmap
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