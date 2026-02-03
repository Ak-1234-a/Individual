package com.example.dreamteam

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class DetailActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detail)

        val txtName = findViewById<TextView>(R.id.txtName)
        val txtDescription = findViewById<TextView>(R.id.txtDescription)

        txtName.text = intent.getStringExtra("name")
        txtDescription.text = intent.getStringExtra("description")
    }
}
