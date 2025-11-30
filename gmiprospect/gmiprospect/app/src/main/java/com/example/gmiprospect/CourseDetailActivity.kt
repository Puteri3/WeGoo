package com.example.gmiprospect

import android.os.Bundle
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity

class CourseDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_course_detail)
        val name = intent.getStringExtra("course_name") ?: "Course"
        findViewById<TextView>(R.id.tv_course_name).text = name
        // Set hardcoded details for now or fetch from local JSON/db
    }class CourseDetailActivity : AppCompatActivity() {
        override fun onCreate(savedInstanceState: Bundle?) {
            super.onCreate(savedInstanceState)
            setContentView(R.layout.activity_course_detail)

            val name = intent.getStringExtra("course_name") ?: "Course"
            val tvName = findViewById<TextView>(R.id.tv_course_name)
            val tvDesc = findViewById<TextView>(R.id.tv_course_desc)

            tvName.text = name

            // Contoh ringkasan (anda boleh customise untuk setiap kursus)
            val description = when (name) {
                "Mechatronics Engineering Technology" -> "Program Mechatronics menggabungkan mechanical, elektronik & kawalan..."
                "Engineering Technology (Instrumentation & Control)" -> "Program ini memberi tumpuan kepada sistem kawalan dan instrumen pengukuran..."
                // tambah untuk semua kursus ...
                else -> "Maklumat kursus akan dikemas kini."
            }
            tvDesc.text = description
        }
    }

}
