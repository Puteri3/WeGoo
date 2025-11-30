package com.example.gmiprospect

import android.graphics.Color
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.ForegroundColorSpan
import android.text.style.UnderlineSpan
import android.view.View
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class CoursesActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_courses)

        // Setup dropdowns
        setupDropdown(R.id.header_mech, R.id.content_mech)
        setupDropdown(R.id.header_elect, R.id.content_elect)
        setupDropdown(R.id.header_comp, R.id.content_comp)
        setupDropdown(R.id.header_gufp, R.id.content_gufp)
        setupDropdown(R.id.header_gapp, R.id.content_gapp)

        // Setup course requirement click listeners
        setupRequirements()
    }

    private fun setupDropdown(headerId: Int, contentId: Int) {
        val header = findViewById<TextView>(headerId)
        val content = findViewById<LinearLayout>(contentId)

        header.setOnClickListener {
            if (content.visibility == View.VISIBLE) {
                content.visibility = View.GONE
                header.text = header.text.toString().replace(" ▴", " ▾")
            } else {
                content.visibility = View.VISIBLE
                header.text = header.text.toString().replace(" ▾", " ▴")
            }
        }
    }

    private fun setupRequirements() {
        // Map department content IDs to their course requirements
        val courseRequirements = mapOf(
            R.id.content_mech to arrayOf(
                "MBOT Standard:\n• SPM with 3 credits in ANY subjects\n• OR O-Level Grade C in 3 subjects\n• OR Pre-Diploma / SKM Level 3 / Certificate Level 3\n• No colour blindness; must pass medical & urine test",
                "MBOT Standard:\n• SPM with 3 credits in ANY subjects\n• OR O-Level Grade C in 3 subjects\n• OR Pre-Diploma / SKM Level 3 / Certificate Level 3\n• No colour blindness; must pass medical & urine test",
                "Mechanical Engineering (MQA Standard):\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects (incl. Mathematics)\n• OR UEC Grade B in 3 subjects\n• OR Certificate / SKM Level 3 with CGPA 2.00\n• No colour blindness; must pass medical & urine test",
                "Mechanical Engineering (MQA Standard):\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects\n• OR UEC Grade B in 3 subjects\n• OR Certificate / SKM Level 3 with CGPA 2.00\n• No colour blindness; must pass medical & urine test",
                "Mechanical Engineering (MQA Standard):\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects\n• OR UEC Grade B in 3 subjects\n• OR Certificate / SKM Level 3 with CGPA 2.00\n• No colour blindness; must pass medical & urine test",
                "Mechanical Engineering (MQA Standard):\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects\n• OR UEC Grade B in 3 subjects\n• OR Certificate / SKM Level 3 with CGPA 2.00\n• No colour blindness; must pass medical & urine test",
                "Mechanical Engineering (MQA Standard):\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects\n• OR UEC Grade B in 3 subjects\n• OR Certificate / SKM Level 3 with CGPA 2.00\n• No colour blindness; must pass medical & urine test"
            ),
            R.id.content_elect to arrayOf(
                "ETAC Standard:\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects incl. Mathematics\n• OR SKM Level 3 + bridging\n• No colour blindness; must pass medical & urine test",
                "ETAC Standard:\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects incl. Mathematics\n• OR SKM Level 3 + bridging\n• No colour blindness; must pass medical & urine test",
                "ETAC Standard:\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects incl. Mathematics\n• OR SKM Level 3 + bridging\n• No colour blindness; must pass medical & urine test",
                "ETAC Standard:\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects incl. Mathematics\n• OR SKM Level 3 + bridging\n• No colour blindness; must pass medical & urine test",
                "ETAC Standard:\n• SPM with 3 credits: Mathematics + 1 Science/Technical + 1 other\n• OR O-Level Grade C in 3 subjects incl. Mathematics\n• OR SKM Level 3 + bridging\n• No colour blindness; must pass medical & urine test"
            ),
            R.id.content_comp to arrayOf(
                "• SPM 3 credits including Mathematics\n• OR O-Level Grade C in 3 subjects incl. Mathematics\n• OR STPM / SKM / Certificate Level 3\n• Mathematics reinforcement may be required",
                "• SPM 3 credits (ANY subjects)\n• OR O-Level Grade C in 3 subjects\n• OR Pre-diploma / SKM Level 3 / Certificate Level 3",
                "• SPM 3 credits OR O-Level Grade C in 3 subjects incl. Math\n• OR Creative Multimedia Certificate\n• OR SKM Level 3 with SPM 1 credit"
            ),
            R.id.content_gufp to arrayOf(
                "GUFP Requirement:\n• SPM Grade C in: BM, Math, English, Add Math, Physics, Chemistry\n• OR O-Level/IGCSE Grade C in: Math, Physics, Chemistry + 2 subjects\n• SPM Trial Accepted"
            ),
            R.id.content_gapp to arrayOf(
                "GAPP Requirement:\nSponsored Candidate:\n• SPM Grade A in: English, Math, Add Math, Physics, Chemistry, + 2 subjects\n\nPrivate Candidate:\n• SPM Grade C in: English, Math, Add Math, Physics, Chemistry + 2 subjects"
            )
        )

        // Iterate all content layouts
        for ((contentId, reqArray) in courseRequirements) {
            val contentLayout = findViewById<LinearLayout>(contentId)
            var index = 0
            for (i in 0 until contentLayout.childCount) {
                val child = contentLayout.getChildAt(i)
                if (child is TextView && child.text.startsWith("•")) {
                    val req = if (index < reqArray.size) reqArray[index] else "Requirement not found"
                    
                    child.setOnClickListener {
                        // Change color to Blue and Underline ONLY when clicked
                        val spannable = SpannableString(child.text.toString())
                        spannable.setSpan(UnderlineSpan(), 0, spannable.length, 0)
                        spannable.setSpan(ForegroundColorSpan(Color.BLUE), 0, spannable.length, 0)
                        child.text = spannable
                        
                        showRequirement(child.text.toString().removePrefix("• ").trim(), req)
                    }
                    index++
                }
            }
        }
    }

    private fun showRequirement(title: String, message: String) {
        AlertDialog.Builder(this)
            .setTitle(title)
            .setMessage(message)
            .setPositiveButton("OK", null)
            .show()
    }
}
