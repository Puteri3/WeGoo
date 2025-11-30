package com.example.gmiprospect

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.ListView
import androidx.appcompat.app.AppCompatActivity

class CoursesActivity : AppCompatActivity() {

    private val courses = listOf(
        "Mechatronics Engineering Technology",
        "Engineering Technology (Instrumentation & Control)",
        "Electronics Engineering Technology (Computer)",
        "Autotronics Engineering Technology",
        "Engineering Technology (Sustainable Energy & Power Distribution)",
        "Industrial Communication System",
        "Facilities & Building Automation",
        "Industrial Plant Maintenance",
        "Engineering Technology (Industrial Design)",
        "Industrial Quality Engineering Engineering Technology",
        "Innovative Product Design Engineering Technology",
        "Mechanical Engineering Technology (CNC Precision)",
        "Engineering Technology (Machine Tools Maintenance)",
        "Mechanical Engineering Technology (Manufacturing)",
        "Precision Tooling Engineering Technology",
        "Software Engineering",
        "Cyber Security Technology",
        "Creative Multimedia"
    )

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)

        val listView = findViewById<ListView>(R.id.list_courses)
        val adapter = ArrayAdapter(this, android.R.layout.simple_list_item_1, courses)
        listView.adapter = adapter

        listView.setOnItemClickListener { _, _, position, _ ->
            val selectedCourse = courses[position]
            val intent = Intent(this, CourseDetailActivity::class.java)
            intent.putExtra("course_name", selectedCourse)
            startActivity(intent)
        }
    }
}
