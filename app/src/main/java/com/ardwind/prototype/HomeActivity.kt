package com.ardwind.prototype

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.auth.FirebaseAuth

class HomeActivity : AppCompatActivity() {

    private lateinit var textfullname: TextView
    private lateinit var textemail: TextView
    private lateinit var btnLogout: ImageView
    private val firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_home)

        setupWindowInsets()
        initializeViews()
        setupClickListeners()
        checkUserLoggedIn()
    }

    private fun setupWindowInsets() {
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
    }

    private fun initializeViews() {
        textfullname = findViewById(R.id.fullname)
        textemail = findViewById(R.id.mail)
        btnLogout = findViewById(R.id.icLogout)
    }

    private fun setupClickListeners() {
        findViewById<TextView>(R.id.menu1).setOnClickListener { navigateToRulesActivity() }
        findViewById<TextView>(R.id.menu2).setOnClickListener { navigateToViewActivity() }
        findViewById<TextView>(R.id.menu3).setOnClickListener { navigateToBookActivity() }
        findViewById<TextView>(R.id.menu4).setOnClickListener { navigateToReportActivity() }
        findViewById<TextView>(R.id.menu5).setOnClickListener { navigateToListActivity() }
        findViewById<TextView>(R.id.menu6).setOnClickListener { navigateToFeedActivity() }
        findViewById<ImageView>(R.id.btnbackhome).setOnClickListener { navigateToLoginActivity() }
        btnLogout.setOnClickListener {
            firebaseAuth.signOut()
            startActivity(Intent(this, LoginActivity::class.java))
            finish()
        }
    }

    private fun checkUserLoggedIn() {
        val firebaseUser = firebaseAuth.currentUser
        if (firebaseUser != null) {
            textfullname.text = firebaseUser.displayName
            textemail.text = firebaseUser.email
        } else {
            startActivity(Intent(this, LoginActivity::class.java))
        }
    }

    private fun navigateToRulesActivity() {
        startActivity(Intent(this, RulesActivity::class.java))
    }

    private fun navigateToViewActivity() {
        startActivity(Intent(this, ViewActivity::class.java))
    }

    private fun navigateToBookActivity() {
        startActivity(Intent(this, BookActivity::class.java))
    }

    private fun navigateToReportActivity() {
        startActivity(Intent(this, ReportActivity::class.java))
    }

    private fun navigateToListActivity() {
        startActivity(Intent(this, ListActivity::class.java))
    }

    private fun navigateToFeedActivity() {
        startActivity(Intent(this, FeedBackActivity::class.java))
    }

    private fun navigateToLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
    }
}