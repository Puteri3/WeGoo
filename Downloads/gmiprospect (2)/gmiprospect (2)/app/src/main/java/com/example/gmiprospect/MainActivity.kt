package com.example.gmiprospect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        findViewById<Button>(R.id.btn_courses).setOnClickListener {
            startActivity(Intent(this, CoursesActivity::class.java))
        }

        findViewById<Button>(R.id.btn_eligibility).setOnClickListener {
            startActivity(Intent(this, EligibilityActivity::class.java))
        }

        findViewById<Button>(R.id.btn_enquiry).setOnClickListener {
            startActivity(Intent(this, EnquiryActivity::class.java))
        }

        // Quick link to online application (optional)
        findViewById<Button>(R.id.btn_apply_online).setOnClickListener {
            val appUrl = "https://gmi.vialing.com/oa/login"
            startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)))
        }
    }
}
