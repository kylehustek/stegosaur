package com.example.stegosaur

import android.annotation.SuppressLint
import android.content.ContentValues
import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import android.view.inputmethod.InputMethod
import android.view.inputmethod.InputMethodManager
import android.widget.*
import com.google.zxing.*
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.w3c.dom.Text


class QRGen : AppCompatActivity() {
    private var editText: EditText? = null
    private var button: Button? = null

    @SuppressLint("ServiceCast")
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

            val bitmap = generateQRCode((this.editText as EditText).text.toString())
            imageToStorage(this, bitmap as Bitmap)
            val currentView = this.currentFocus
            if(currentView != null){
                val inMethod = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                inMethod?.hideSoftInputFromWindow(currentView.windowToken, 0)
            }
            val qrRes: ImageView = findViewById<ImageView>(R.id.qr_res)
            qrRes.setImageBitmap(bitmap)
            Toast.makeText(this, "QR Code saved to Pictures/Stegosaur", Toast.LENGTH_LONG).show()

            val qrString = decodeQR(bitmap)
            val qrOut = findViewById<TextView>(R.id.qr_content)
            qrOut.setText(qrString)

        }

    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun decodeQR(bitmap: Bitmap): String{
        val qrWidth = bitmap.width
        val qrHeight = bitmap.height
        val pix = IntArray(qrHeight * qrWidth)
        bitmap.getPixels(pix, 0, qrWidth, 0, 0, qrWidth, qrHeight)
        val rgbLuminanceSource = RGBLuminanceSource(qrWidth, qrHeight, pix)
        val binaryBitmap: BinaryBitmap = BinaryBitmap(HybridBinarizer(rgbLuminanceSource))
        val reader: QRCodeReader = QRCodeReader()
        val result = reader.decode(binaryBitmap)
        return result.text
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