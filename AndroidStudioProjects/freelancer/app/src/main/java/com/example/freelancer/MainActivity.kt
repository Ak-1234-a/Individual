package com.example.freelancer

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {

    lateinit var db: DatabaseHelper

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val sharedPref = getSharedPreferences("LoginPref", Context.MODE_PRIVATE)
        val isLoggedIn = sharedPref.getBoolean("isLoggedIn", false)

        if (isLoggedIn) {
            startActivity(Intent(this, home::class.java))
            finish()
        }

        setContentView(R.layout.activity_main)

        db = DatabaseHelper(this)

        val email = findViewById<EditText>(R.id.usernameEditText)
        val password = findViewById<EditText>(R.id.passwordEditText)
        val loginBtn = findViewById<Button>(R.id.loginButton)
        val registerBtn = findViewById<Button>(R.id.registerButton)

        loginBtn.setOnClickListener {

            val emailText = email.text.toString()
            val passText = password.text.toString()

            if (emailText.isEmpty() || passText.isEmpty()) {
                Toast.makeText(this, "All fields required", Toast.LENGTH_SHORT).show()
            } else {

                if (db.checkUser(emailText, passText)) {

                    val editor = sharedPref.edit()
                    editor.putBoolean("isLoggedIn", true)
                    editor.putString("email", emailText)
                    editor.apply()

                    Toast.makeText(this, "Login Successful", Toast.LENGTH_SHORT).show()
                    startActivity(Intent(this, home::class.java))
                    finish()

                } else {
                    Toast.makeText(this, "Invalid Email or Password", Toast.LENGTH_SHORT).show()
                }
            }
        }

        registerBtn.setOnClickListener {
            startActivity(Intent(this, register::class.java))
        }
    }
}