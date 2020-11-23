package com.example.stegosaur

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.os.PersistableBundle
import android.provider.MediaStore
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.google.zxing.BinaryBitmap
import com.google.zxing.RGBLuminanceSource
import com.google.zxing.common.HybridBinarizer
import com.google.zxing.qrcode.QRCodeReader
import org.w3c.dom.Text
import java.io.InputStream

class DecodeImage : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.decode_image)

        val actionBar = supportActionBar
        actionBar!!.title = "Decode Image"
        actionBar.setDisplayHomeAsUpEnabled (true)


        val button: Button = findViewById<Button>(R.id.decode_image_button)
        button.setOnClickListener{
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.INTERNAL_CONTENT_URI)
            intent.type = "image/*"
            startActivityForResult(intent, 1)
        }

    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        val img_uri : Uri? = data?.data
        val input: InputStream? = img_uri?.let { contentResolver.openInputStream(it) }
        val bitmap: Bitmap = BitmapFactory.decodeStream(input)
        val messageQr: Bitmap = Steganography.decode(bitmap)

        val imageView : ImageView = findViewById(R.id.decoded_image)
        imageView.setImageBitmap(messageQr)

        val textOut: String = decodeQR(messageQr)
        val textView: TextView = findViewById<TextView>(R.id.decode_content)
        textView.setText(textOut)

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



}