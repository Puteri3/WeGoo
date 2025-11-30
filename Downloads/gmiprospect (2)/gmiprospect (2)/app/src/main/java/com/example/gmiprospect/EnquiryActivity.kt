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

        // Call Admissions
        findViewById<Button>(R.id.btn_call).setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+60389219000")
            startActivity(intent)
        }

        // Email Admissions
        findViewById<Button>(R.id.btn_email).setOnClickListener {
            val intent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:marketing@gmi.edu.my")
                putExtra(Intent.EXTRA_SUBJECT, "Prospect Enquiry")
                putExtra(Intent.EXTRA_TEXT, "Hello, I would like to ask about...")
            }
            startActivity(intent)
        }

        // View Map to GMI
        findViewById<Button>(R.id.btn_map).setOnClickListener {
            val mapIntent = Intent(Intent.ACTION_VIEW).apply {
                data = Uri.parse("https://www.google.com/maps/place/German-Malaysian+Institute")
            }
            startActivity(mapIntent)
        }

        // Enquiry & Feedback (can open email or feedback form)
        findViewById<Button>(R.id.btn_feedback).setOnClickListener {
            val feedbackIntent = Intent(Intent.ACTION_SENDTO).apply {
                data = Uri.parse("mailto:marketing@gmi.edu.my") // or feedback form URL
                putExtra(Intent.EXTRA_SUBJECT, "GMI Feedback / Enquiry")
                putExtra(Intent.EXTRA_TEXT, "Hello, I would like to provide feedback or ask a question...")
            }
            startActivity(feedbackIntent)
        }
    }
}
