package com.example.dreamteam

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val imgMember1 = findViewById<ImageView>(R.id.imgmember1)

        imgMember1.setOnClickListener {
            val intent = Intent(this, DetailActivity::class.java)
            intent.putExtra("name", "Arun")
            intent.putExtra("description", "Arun is the team leader and Android developer.")
            startActivity(intent)
        }
    }
}
