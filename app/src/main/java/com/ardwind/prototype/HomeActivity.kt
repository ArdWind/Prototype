package com.ardwind.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat

class HomeActivity : AppCompatActivity() {

    private fun navigateToRulesActivity() {
        val intent = Intent(this, RulesActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToViewActivity() {
        val intent = Intent(this, ViewActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToBookActivity() {
        val intent = Intent(this, BookActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToReportActivity() {
        val intent = Intent(this, ReportActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToListActivity() {
        val intent = Intent(this, ListActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToFeedActivity() {
        val intent = Intent(this, FeedBackActivity::class.java)
        startActivity(intent)
    }

    private fun navigateToLoginActivity() {
        val intent = Intent(this, LoginActivity::class.java)
        startActivity(intent)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        val btnRules1 = findViewById<TextView>(R.id.menu1)
        btnRules1.setOnClickListener {
            navigateToRulesActivity()
        }

        val btnRules2 = findViewById<TextView>(R.id.menu2)
        btnRules2.setOnClickListener {
            navigateToViewActivity()
        }

        val btnRules3 = findViewById<TextView>(R.id.menu3)
        btnRules3.setOnClickListener {
            navigateToBookActivity()
        }

        val btnRules4 = findViewById<TextView>(R.id.menu4)
        btnRules4.setOnClickListener {
            navigateToReportActivity()
        }

        val btnRules5 = findViewById<TextView>(R.id.menu5)
        btnRules5.setOnClickListener {
            navigateToListActivity()
        }

        val btnRules6 = findViewById<TextView>(R.id.menu6)
        btnRules6.setOnClickListener {
            navigateToFeedActivity()
        }

        val loginRules = findViewById<ImageView>(R.id.btnbackhome)
        loginRules.setOnClickListener {
            navigateToLoginActivity()
        }
    }
}