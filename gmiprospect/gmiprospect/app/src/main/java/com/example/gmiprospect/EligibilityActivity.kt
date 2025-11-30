package com.example.gmiprospect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EligibilityActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eligibility)

        val etScore = findViewById<EditText>(R.id.et_score) // numeric score or GPA
        val spinnerCourse = findViewById<Spinner>(R.id.spinner_course)
        val btnCheck = findViewById<Button>(R.id.btn_check)

        // Example: course -> minimum score mapping
        val courseList = listOf("Diploma: Software Eng (Min 50%)", "Diploma: Mechatronics (Min 55%)",
            "GAPP: Pre-Dip (Min 45%)", "GUFP: Foundation (Min 40%)")
        val adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseList)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCourse.adapter = adapter

        btnCheck.setOnClickListener {
            val scoreText = etScore.text.toString().trim()
            if (scoreText.isEmpty()) {
                Toast.makeText(this, "Please enter your score/percentage", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
            val score = scoreText.toDoubleOrNull()
            if (score == null) {
                Toast.makeText(this, "Invalid number", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }
// In EligibilityActivity.kt

            val courseList = listOf(
                "Mechatronics Engineering Technology",
                "Instrumentation & Control",
                "Electronics Engineering Technology (Computer)",
                "Sustainable Energy & Power Distribution",
                "Industrial Communication System",
                "Industrial Design Engineering",
                "Industrial Quality Engineering",
                "Innovative Product Design Engineering",
                "Mechanical Engineering (CNC Precision)",
                "Mechanical Engineering (Manufacturing)",
                "Precision Tooling Engineering",
                "Software Engineering",
                "Cyber Security Technology",
                "Creative Multimedia"
            )

            val minRequirements = mapOf(
                0 to 50.0,  // Mechatronics
                1 to 50.0,  // Instrumentation
                2 to 50.0,  // Electronics (Computer)
                3 to 50.0,  // Sustainable Energy
                4 to 45.0,  // Industrial Communication
                5 to 45.0,  // Industrial Design
                6 to 50.0,  // Quality Engineering
                7 to 45.0,  // Innovative Product Design
                8 to 55.0,  // CNC Precision
                9 to 55.0,  // Manufacturing
                10 to 55.0, // Precision Tooling
                11 to 50.0, // Software Engineering
                12 to 50.0, // Cyber Security
                13 to 45.0  // Creative Multimedia
            )

            spinnerCourse.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_item, courseList)

// use minRequirements[spinnerCourse.selectedItemPosition] to find minimum required score

            val courseSelected = spinnerCourse.selectedItemPosition
            val minReq = when (courseSelected) {
                0 -> 50.0
                1 -> 55.0
                2 -> 45.0
                else -> 40.0
            }

            if (score >= minReq) {
                AlertDialog.Builder(this)
                    .setTitle("Eligible")
                    .setMessage("You meet the minimum requirement (${minReq}%). Proceed to online application?")
                    .setPositiveButton("Open Application") { _, _ ->
                        val appUrl = "https://gmi.vialing.com/oa/login"
                        startActivity(Intent(Intent.ACTION_VIEW, Uri.parse(appUrl)))
                    }
                    .setNegativeButton("Cancel", null)
                    .show()
            } else {
                AlertDialog.Builder(this)
                    .setTitle("Not Eligible")
                    .setMessage("Your score: $score. Minimum for this course: ${minReq}%. Consider other courses or improve results.")
                    .setPositiveButton("See Alternatives") { _, _ ->
                        // For simplicity, return to courses list
                        startActivity(Intent(this, CoursesActivity::class.java))
                    }
                    .setNegativeButton("Close", null)
                    .show()
            }
        }
    }
}
