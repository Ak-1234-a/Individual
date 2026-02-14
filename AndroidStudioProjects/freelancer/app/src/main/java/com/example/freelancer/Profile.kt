package com.example.freelancer

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import android.widget.Button
import android.widget.PopupMenu
import android.widget.Toast

class Profile : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_profile)

        val btnEditProfile = findViewById<Button>(R.id.btnEditProfile)

        btnEditProfile.setOnClickListener {

            val popup = PopupMenu(this, btnEditProfile)
            popup.menuInflater.inflate(R.menu.edit_profile_menu, popup.menu)

            popup.setOnMenuItemClickListener { item ->
                when (item.itemId) {

                    R.id.change_name -> {
                        Toast.makeText(this, "Change Name Clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.change_email -> {
                        Toast.makeText(this, "Change Email Clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    R.id.change_password -> {
                        Toast.makeText(this, "Change Password Clicked", Toast.LENGTH_SHORT).show()
                        true
                    }

                    else -> false
                }
            }

            popup.show()
        }

    }
}