package com.example.freelancer

import android.Manifest
import android.app.*
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.view.*
import android.widget.Button
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.database.FirebaseDatabase
import java.util.*

class home : AppCompatActivity() {

    private val CHANNEL_ID = "notification_channel"
    private val NOTIFICATION_ID = 1

    private var fromDate = ""
    private var fromTime = ""
    private var toDate = ""
    private var toTime = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val btnWork = findViewById<Button>(R.id.btnWork)
        registerForContextMenu(btnWork)

        btnWork.setOnClickListener {
            pickFromDate()
        }

        createNotificationChannel()

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.POST_NOTIFICATIONS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.POST_NOTIFICATIONS),
                    1
                )
            }
        }
    }

    // ---------------- FROM DATE ----------------

    private fun pickFromDate() {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                fromDate = "$day/${month + 1}/$year"
                pickFromTime()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    // ---------------- FROM TIME ----------------

    private fun pickFromTime() {
        val calendar = Calendar.getInstance()

        val dialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                fromTime = String.format("%02d:%02d", hour, minute)
                pickToDate()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        dialog.show()
    }

    // ---------------- TO DATE ----------------

    private fun pickToDate() {
        val calendar = Calendar.getInstance()

        val dialog = DatePickerDialog(
            this,
            { _, year, month, day ->
                toDate = "$day/${month + 1}/$year"
                pickToTime()
            },
            calendar.get(Calendar.YEAR),
            calendar.get(Calendar.MONTH),
            calendar.get(Calendar.DAY_OF_MONTH)
        )
        dialog.show()
    }

    // ---------------- TO TIME ----------------

    private fun pickToTime() {
        val calendar = Calendar.getInstance()

        val dialog = TimePickerDialog(
            this,
            { _, hour, minute ->
                toTime = String.format("%02d:%02d", hour, minute)
                showConfirmationDialog()
            },
            calendar.get(Calendar.HOUR_OF_DAY),
            calendar.get(Calendar.MINUTE),
            true
        )
        dialog.show()
    }

    // ---------------- ALERT DIALOG ----------------

    private fun showConfirmationDialog() {
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Confirm Application")
        builder.setMessage(
            "From:\nDate: $fromDate\nTime: $fromTime\n\n" +
                    "To:\nDate: $toDate\nTime: $toTime\n\nProceed?"
        )

        builder.setPositiveButton("Yes") { dialog, _ ->
            updateScheduleInFirebase()
            dialog.dismiss()
        }

        builder.setNegativeButton("No") { dialog, _ ->
            dialog.dismiss()
        }

        builder.show()
    }

    private fun updateScheduleInFirebase() {
        val sharedPref = getSharedPreferences("LoginPref", Context.MODE_PRIVATE)
        val email = sharedPref.getString("email", "unknown_user") ?: "unknown_user"
        val sanitizedEmail = email.replace(".", ",")

        val database = FirebaseDatabase.getInstance()
        val myRef = database.getReference("project_schedules").child(sanitizedEmail)

        val scheduleData = mapOf(
            "fromDate" to fromDate,
            "fromTime" to fromTime,
            "toDate" to toDate,
            "toTime" to toTime,
            "timestamp" to System.currentTimeMillis()
        )

        myRef.setValue(scheduleData)
            .addOnSuccessListener {
                Toast.makeText(this, "Schedule Updated in Firebase!", Toast.LENGTH_LONG).show()
                showNotification()
            }
            .addOnFailureListener {
                Toast.makeText(this, "Failed to update Firebase: ${it.message}", Toast.LENGTH_LONG).show()
            }
    }

    // ---------------- NOTIFICATION ----------------

    private fun showNotification() {
        val intent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(
            this, 0, intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.ic_dialog_alert)
            .setContentTitle("Notification")
            .setContentText("Project schedule updated successfully.")
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
            .build()

        if (ActivityCompat.checkSelfPermission(
                this,
                Manifest.permission.POST_NOTIFICATIONS
            ) != PackageManager.PERMISSION_GRANTED
        ) return

        NotificationManagerCompat.from(this)
            .notify(NOTIFICATION_ID, notification)
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Notification Channel",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Channel for status notifications"
            }

            val manager =
                getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            manager.createNotificationChannel(channel)
        }
    }

    // ---------------- CONTEXT MENU ----------------

    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)

        if (v?.id == R.id.btnWork) {
            menuInflater.inflate(R.menu.work_menu, menu)
            menu?.setHeaderTitle("Choose Action")
        }
    }

    override fun onContextItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {

            R.id.apply -> {
                Toast.makeText(this, "Applied Successfully", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.save -> {
                updateScheduleInFirebase()
                true
            }

            R.id.share -> {
                Toast.makeText(this, "Project Shared", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    // ---------------- OPTIONS MENU ----------------

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile ->
                startActivity(Intent(this, Profile::class.java))

            R.id.settings ->
                Toast.makeText(this, "Settings Clicked", Toast.LENGTH_SHORT).show()

            R.id.help ->
                Toast.makeText(this, "Help Clicked", Toast.LENGTH_SHORT).show()

            R.id.logout ->{
                // Clear SharedPreferences
                val sharedPref = getSharedPreferences("LoginPref", Context.MODE_PRIVATE)
                val editor = sharedPref.edit()
                editor.clear()
                editor.apply()

                Toast.makeText(this, "Logged Out Successfully", Toast.LENGTH_SHORT).show()

                // Go back to Login Page
                startActivity(Intent(this, MainActivity::class.java))
                finish()
                return true;
            }
        }
        return true
    }
}
