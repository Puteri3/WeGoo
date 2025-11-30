package com.example.gmiprospect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.Button
import androidx.appcompat.app.AppCompatActivity

class EnquiryActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_enquiry)

        findViewById<Button>(R.id.btn_call).setOnClickListener {
            val phone = "tel:+60312345678" // replace with official GMI number
            startActivity(Intent(Intent.ACTION_DIAL, Uri.parse(phone)))
        }

        findViewById<Button>(R.id.btn_email).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:admissions@gmi.edu.my") // replace with actual email
                putExtra(Intent.EXTRA_SUBJECT, "Prospect Enquiry")
                putExtra(Intent.EXTRA_TEXT, "Hello, I would like to ask about...")
            }
            startActivity(intent)
        }
    }
}
