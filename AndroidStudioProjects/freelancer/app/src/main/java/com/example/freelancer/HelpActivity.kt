package com.example.freelancer

import android.os.Bundle
import android.text.util.Linkify
import android.view.View
import android.widget.Button
import android.widget.ProgressBar
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import org.json.JSONObject

class HelpActivity : AppCompatActivity() {

    private lateinit var tvHelpResult: TextView
    private lateinit var progressBar: ProgressBar

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_help)

        val toolbar = findViewById<Toolbar>(R.id.toolbarHelp)
        setSupportActionBar(toolbar)
        supportActionBar?.setDisplayHomeAsUpEnabled(true)
        toolbar.setNavigationOnClickListener { finish() }

        tvHelpResult = findViewById(R.id.tvHelpResult)
        progressBar = findViewById(R.id.progressBar)
        val btnFetchHelp = findViewById<Button>(R.id.btnFetchHelp)

        btnFetchHelp.setOnClickListener {
            fetchHelpContentInEnglish()
        }
    }

    private fun fetchHelpContentInEnglish() {
        progressBar.visibility = View.VISIBLE
        tvHelpResult.text = "Connecting to Help Center..."

        // Simulating a Web Service call with English JSON data
        Thread {
            try {
                // Mocking the network delay
                Thread.sleep(1500)

                // This is a sample JSON string in English related to the Freelancer app
                val mockJsonResponse = """
                    {
                        "support_id": 101,
                        "category": "Payment Issues",
                        "faq_title": "How to withdraw your earnings",
                        "instructions": "To withdraw your earnings, go to the Wallet section in your profile, select 'Withdraw', and choose your preferred bank account or digital wallet. Transfers typically take 3-5 business days.",
                        "support_contact": "support@freelancerapp.com",
                        "website": "https://www.freelancer-support.com"
                    }
                """.trimIndent()

                // JSON Parsing
                val jsonObject = JSONObject(mockJsonResponse)
                val title = jsonObject.getString("faq_title")
                val category = jsonObject.getString("category")
                val instructions = jsonObject.getString("instructions")
                val contact = jsonObject.getString("support_contact")
                val website = jsonObject.getString("website")

                // Formatting the English output
                val formattedHelp = "Category: $category\n\n" +
                        "Q: $title\n\n" +
                        "A: $instructions\n\n" +
                        "Official Site: $website\n\n" +
                        "Support Email: $contact"

                runOnUiThread {
                    progressBar.visibility = View.GONE
                    tvHelpResult.text = formattedHelp
                    // Apply Linkify to make the website and email clickable
                    Linkify.addLinks(tvHelpResult, Linkify.ALL)
                }
            } catch (e: Exception) {
                runOnUiThread {
                    progressBar.visibility = View.GONE
                    tvHelpResult.text = "Failed to fetch English help data."
                }
            }
        }.start()
    }
}
