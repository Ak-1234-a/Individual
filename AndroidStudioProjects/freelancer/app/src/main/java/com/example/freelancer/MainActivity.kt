package com.example.freelancer

import android.content.Intent
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import android.view.ContextMenu
import android.view.View
import android.widget.Button
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import com.example.freelancer.Profile
import com.example.freelancer.R



class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)

        val toolbar = findViewById<Toolbar>(R.id.toolbar)
        setSupportActionBar(toolbar)

        val btnWork = findViewById<Button>(R.id.btnWork)
        registerForContextMenu(btnWork)
        btnWork.setOnClickListener{
            showAlertDialog();
        }

    }

    private fun showAlertDialog(){
        val builder = AlertDialog.Builder(this)
        builder.setTitle("Alert")
        builder.setMessage("Are you sure you want to apply?")
        builder.setPositiveButton("Yes"){dialog,_->
            dialog.dismiss()
        }
        builder.setNegativeButton("No"){dialog,_->
            dialog.dismiss()
        }
        builder.show()
    }
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
                Toast.makeText(this, "Project Saved", Toast.LENGTH_SHORT).show()
                true
            }

            R.id.share -> {
                Toast.makeText(this, "Project Shared", Toast.LENGTH_SHORT).show()
                true
            }

            else -> super.onContextItemSelected(item)
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.profile ->
                startActivity(Intent(this, Profile::class.java))

            R.id.settings ->
                true

            R.id.help ->
                true

            R.id.logout ->
                true
        }
        return true
    }
}
