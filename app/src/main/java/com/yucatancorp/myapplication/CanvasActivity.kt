package com.yucatancorp.myapplication

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import com.yucatancorp.myapplication.customviews.CanvasField

class CanvasActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_canvas)
        val prueba = ""
        val uriReceived = intent.getStringExtra("photoUri") ?: prueba
        val canvas: CanvasField = findViewById(R.id.canvasField)
        canvas.uri = uriReceived
        val button: Button = findViewById(R.id.button_mine)
        button.setOnClickListener {
            startActivity(Intent(this, GaleryActivity::class.java))
        }
    }

}