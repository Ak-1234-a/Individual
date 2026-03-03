package com.example.freelancer

import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class register : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)

        db = DatabaseHelper(this)

        val name = findViewById<EditText>(R.id.nameEditText)
        val email = findViewById<EditText>(R.id.emailEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val registerBtn = findViewById<Button>(R.id.registerButton)

        registerBtn.setOnClickListener {

            val nameText = name.text.toString()
            val emailText = email.text.toString()
            val passText = password.text.toString()

            if (nameText.isEmpty() || emailText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            } else {
                if (db.checkEmail(emailText)) {
                    Toast.makeText(this, "Email already exists", Toast.LENGTH_SHORT).show()
                } else {
                    val inserted = db.insertUser(nameText, emailText, passText)
                    if (inserted) {
                        Toast.makeText(this, "Registered Successfully", Toast.LENGTH_SHORT).show()
                        startActivity(Intent(this, MainActivity::class.java))
                        finish()
                    } else {
                        Toast.makeText(this, "Registration Failed", Toast.LENGTH_SHORT).show()
                    }
                }
            }
        }


    }
}