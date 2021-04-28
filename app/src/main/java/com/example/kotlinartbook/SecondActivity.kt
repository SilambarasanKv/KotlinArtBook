package com.example.kotlinartbook

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.provider.MediaStore
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import android.Manifest
import android.app.Activity
import android.content.Context
import android.graphics.Bitmap
import android.os.Build
import androidx.annotation.RequiresApi
import kotlinx.android.synthetic.main.activity_second.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class SecondActivity : AppCompatActivity() {

    private var selectedImage: Bitmap? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        var selectedImage : Bitmap? = null

    }

    @RequiresApi(Build.VERSION_CODES.M)
    fun select(view: View) {

        if (checkSelfPermission(Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            requestPermissions(arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 2)
        } else {
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, 1)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {

        if (requestCode == 2) {
            if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
                startActivityForResult(intent, 1)
            }
        }

        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {

        if (requestCode == 1 && resultCode == Activity.RESULT_OK && data != null) {
            val image = data.data

            try {
                selectedImage = MediaStore.Images.Media.getBitmap(this.contentResolver, image)
                imageView.setImageBitmap(selectedImage)
            } catch(e: Exception) {
                e.printStackTrace()
            }

                }

        super.onActivityResult(requestCode, resultCode, data)
    }

    fun save(view: View) {

        val artName = editText.text.toString()

        val outputStream = ByteArrayOutputStream()
        selectedImage?.compress(Bitmap.CompressFormat.JPEG, 50, outputStream)
        val byteArray = outputStream.toByteArray()

        try {

            val database = this.openOrCreateDatabase("Arts", Context.MODE_PRIVATE, null)
            database.execSQL("CREATE TABLE IF NOT EXISTS arts (name VARCHAR, image BLOB)")

            val sqlString = "INSERT INTO arts (name, image) VALUES (?, ?)"
            val statement = database.compileStatement(sqlString)

            statement.bindString(1, artName)
            statement.bindBlob(2, byteArray)
            statement.execute()

        } catch(e: Exception) {
            e.printStackTrace()
        }

        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)

    }


}