package com.example.gmiprospect

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity

class EligibilityActivity : AppCompatActivity() {

    // Main components
    private lateinit var spinnerQualification: Spinner
    private lateinit var btnCheck: Button

    // SPM Section
    private lateinit var layoutSpm: LinearLayout
    private lateinit var spinnerCandidateType: Spinner
    private lateinit var spinnerSpmBm: Spinner
    private lateinit var spinnerSpmSejarah: Spinner
    private lateinit var spinnerSpmEnglish: Spinner
    private lateinit var spinnerSpmMath: Spinner
    private lateinit var spinnerSpmAddMath: Spinner
    private lateinit var spinnerSpmPhysics: Spinner
    private lateinit var spinnerSpmChemistry: Spinner
    private lateinit var spinnerSpmScience: Spinner

    // Other Section
    private lateinit var layoutOther: LinearLayout
    private lateinit var etCgpa: EditText
    private lateinit var etMath: EditText
    private lateinit var etScience: EditText

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_eligibility)

        // Bind views
        spinnerQualification = findViewById(R.id.spinner_qualification)
        btnCheck = findViewById(R.id.btn_check)

        layoutSpm = findViewById(R.id.layout_spm)
        spinnerCandidateType = findViewById(R.id.spinner_candidate_type)
        spinnerSpmBm = findViewById(R.id.spinner_spm_bm)
        spinnerSpmSejarah = findViewById(R.id.spinner_spm_sejarah)
        spinnerSpmEnglish = findViewById(R.id.spinner_spm_english)
        spinnerSpmMath = findViewById(R.id.spinner_spm_math)
        spinnerSpmAddMath = findViewById(R.id.spinner_spm_add_math)
        spinnerSpmPhysics = findViewById(R.id.spinner_spm_physics)
        spinnerSpmChemistry = findViewById(R.id.spinner_spm_chemistry)
        spinnerSpmScience = findViewById(R.id.spinner_spm_science)

        layoutOther = findViewById(R.id.layout_other)
        etCgpa = findViewById(R.id.et_cgpa)
        etMath = findViewById(R.id.et_math)
        etScience = findViewById(R.id.et_science)

        setupSpinners()
        setupVisibilityLogic()

        spinnerQualification.post {
            updateFormVisibility(spinnerQualification.selectedItem.toString())
        }

        btnCheck.setOnClickListener { checkEligibility() }
    }

    private fun setupSpinners() {
        val qualifications = arrayOf("SPM", "O-Level", "Pre-Diploma / SKM / Certificate", "UEC", "STPM", "STAM")
        spinnerQualification.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, qualifications)

        val candidateTypes = arrayOf("None", "Sponsored", "Private")
        spinnerCandidateType.adapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, candidateTypes)

        val grades = arrayOf("A+", "A", "A-", "B+", "B", "C+", "C", "D", "E", "G", "TH", "None")
        val gradeAdapter = ArrayAdapter(this, android.R.layout.simple_spinner_dropdown_item, grades)

        spinnerSpmBm.adapter = gradeAdapter
        spinnerSpmSejarah.adapter = gradeAdapter
        spinnerSpmEnglish.adapter = gradeAdapter
        spinnerSpmMath.adapter = gradeAdapter
        spinnerSpmAddMath.adapter = gradeAdapter
        spinnerSpmPhysics.adapter = gradeAdapter
        spinnerSpmChemistry.adapter = gradeAdapter
        spinnerSpmScience.adapter = gradeAdapter
    }

    private fun setupVisibilityLogic() {
        spinnerQualification.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(parent: AdapterView<*>?, view: View?, pos: Int, id: Long) {
                updateFormVisibility(spinnerQualification.selectedItem.toString())
            }
            override fun onNothingSelected(parent: AdapterView<*>?) {}
        }
    }

    private fun updateFormVisibility(selected: String) {
        if (selected == "SPM") {
            layoutSpm.visibility = View.VISIBLE
            layoutOther.visibility = View.GONE
        } else {
            layoutSpm.visibility = View.GONE
            layoutOther.visibility = View.VISIBLE

            if (selected == "STAM") {
                etMath.hint = "Enter Grade (e.g. Maqbul, Jayyid)"
                etScience.hint = "Enter Grade (e.g. Maqbul, Jayyid)"
            } else {
                etMath.hint = "Enter Math Grade/Score"
                etScience.hint = "Enter Science Grade/Score"
            }
        }
    }

    // -------------------------
    // Helpers
    // -------------------------

    private fun isCredit(grade: String) =
        grade != "None" && (grade.startsWith("A") || grade.startsWith("B") || grade.startsWith("C"))

    private fun isA(grade: String) =
        grade.startsWith("A")

    private fun isPassGrade(grade: String) =
        grade !in listOf("None", "G", "TH")

    private fun isAtLeastB(grade: String) =
        grade.startsWith("A") || grade.startsWith("B")

    // -------------------------
    // SPM CHECK (bullet format)
    // -------------------------

    private fun checkSpmEligibility(): Pair<String, Int> {
        var eligible = 0
        val sb = StringBuilder()

        val bm = spinnerSpmBm.selectedItem.toString()
        val sejarah = spinnerSpmSejarah.selectedItem.toString()
        val eng = spinnerSpmEnglish.selectedItem.toString()
        val math = spinnerSpmMath.selectedItem.toString()
        val addMath = spinnerSpmAddMath.selectedItem.toString()
        val physics = spinnerSpmPhysics.selectedItem.toString()
        val chemistry = spinnerSpmChemistry.selectedItem.toString()
        val science = spinnerSpmScience.selectedItem.toString()
        val type = spinnerCandidateType.selectedItem.toString()

        // ------ If fail Sejarah, cannot apply for anything ------
        if (!isPassGrade(sejarah)) {
            sb.append("• Not eligible for any course: Sejarah is compulsory and must be passed.\n")
            return Pair(sb.toString(), 0)
        }

        val all = listOf(bm, sejarah, eng, math, addMath, physics, chemistry, science)
        val totalCredits = all.count { isCredit(it) }
        val hasMathCredit = isCredit(math) || isCredit(addMath)
        val hasScienceCredit = listOf(physics, chemistry, science).any { isCredit(it) }

        // ------- Mechanical -------
        if (totalCredits >= 3 && hasMathCredit && hasScienceCredit && isPassGrade(eng)) {
            sb.append("• Eligible for Mechanical / Mechatronics / Engineering Technology\n")
            eligible++
        } else sb.append("• Not eligible for Mechanical / Mechatronics / Engineering Technology\n")

        // ------- Software -------
        if (totalCredits >= 3 && hasMathCredit) {
            sb.append("• Eligible for Software Engineering\n")
            eligible++
        } else sb.append("• Not eligible for Software Engineering\n")

        // ------- Cyber / Creative -------
        if (totalCredits >= 3) {
            sb.append("• Eligible for Cyber Security Technology\n")
            sb.append("• Eligible for Creative Multimedia\n")
            eligible += 2
        } else {
            sb.append("• Not eligible for Cyber Security Technology\n")
            sb.append("• Not eligible for Creative Multimedia\n")
        }

        // ------- MBOT -------
        if (totalCredits >= 3) {
            sb.append("• Eligible for MBOT-standard programmes\n")
            eligible++
        } else sb.append("• Not eligible for MBOT-standard programmes\n")

        // ------- GUFP -------
        if (
            isCredit(bm) && isCredit(eng) && hasMathCredit &&
            isCredit(addMath) && isCredit(physics) && isCredit(chemistry)
        ) {
            sb.append("• Eligible for GUFP\n")
            eligible++
        } else sb.append("• Not eligible for GUFP\n")

        // ------- GAPP -------
        if (type == "Sponsored" &&
            listOf(eng, math, addMath, physics, chemistry).all { isA(it) }
        ) {
            sb.append("• Eligible for GAPP Sponsored\n")
            eligible++
        } else if (type == "Private" &&
            listOf(eng, math, addMath, physics, chemistry).all { isCredit(it) }
        ) {
            sb.append("• Eligible for GAPP Private\n")
            eligible++
        } else if (type != "None") {
            sb.append("• Not eligible for GAPP ($type)\n")
        }

        sb.append("\nOther notes:\n")
        sb.append("• Pass Sejarah is compulsory\n")
        sb.append("• SVM: BM C + Pass Sejarah\n")

        return Pair(sb.toString(), eligible)
    }

    // -------------------------
    // O-Level / UEC / SKM / STPM / STAM
    // -------------------------

    private fun checkOtherEligibility(q: String): Pair<String, Int> {
        val math = etMath.text.toString().trim()
        val science = etScience.text.toString().trim()
        val cgpa = etCgpa.text.toString().trim().toDoubleOrNull() ?: 0.0

        var eligible = 0
        val sb = StringBuilder()

        fun pass(input: String): Boolean {
            val n = input.toIntOrNull()
            if (n != null) return n >= 40
            return input.uppercase().startsWith("A")
                    || input.uppercase().startsWith("B")
                    || input.uppercase().startsWith("C")
        }

        fun bOrBetter(input: String): Boolean {
            val n = input.toIntOrNull()
            if (n != null) return n >= 60
            return input.uppercase().startsWith("A") || input.uppercase().startsWith("B")
        }

        when (q) {

            "O-Level" -> {
                if (pass(math)) {
                    sb.append("• Eligible for Engineering Diploma (O-Level)\n")
                    eligible++
                } else sb.append("• Not eligible for Engineering Diploma (O-Level)\n")

                sb.append("\nOther notes:\n")
                sb.append("• O-Level requires Grade C in 3 subjects including Mathematics\n")
            }

            "Pre-Diploma / SKM / Certificate" -> {
                if (cgpa >= 2.0) {
                    sb.append("• Eligible via Certificate/SKM Level 3\n")
                    eligible++
                } else sb.append("• Not eligible via Certificate/SKM (CGPA < 2.0)\n")

                sb.append("\nOther notes:\n")
                sb.append("• Certificate/SKM Level 3 requires CGPA 2.00\n")
            }

            "UEC" -> {
                if (bOrBetter(math) && bOrBetter(science)) {
                    sb.append("• Eligible for Engineering Diploma (UEC)\n")
                    eligible++
                } else sb.append("• Not eligible for Engineering Diploma (UEC)\n")

                sb.append("\nOther notes:\n")
                sb.append("• UEC requires at least Grade B in 3 subjects including Mathematics\n")
            }

            "STPM" -> {
                if (cgpa >= 2.0 && pass(math)) {
                    sb.append("• Eligible for Mechanical/Engineering Technology\n")
                    sb.append("• Eligible for Software Engineering\n")
                    sb.append("• Eligible for MBOT-standard programmes\n")
                    eligible += 3
                } else {
                    sb.append("• Not eligible for Diploma (STPM requirements not met)\n")
                }

                sb.append("\nOther notes:\n")
                sb.append("• STPM requires CGPA 2.00 + pass Mathematics\n")
            }

            "STAM" -> {
                val grade = math.uppercase()
                val maqbul = grade.contains("MAQBUL") || grade.contains("JAYYID") || grade.contains("MUMTAZ")

                if (maqbul) {
                    sb.append("• Eligible for Mechanical/Engineering Technology\n")
                    sb.append("• Eligible for Software Engineering\n")
                    sb.append("• Eligible for Cyber Security\n")
                    sb.append("• Eligible for Creative Multimedia\n")
                    sb.append("• Eligible for MBOT-standard programmes\n")
                    eligible += 5
                } else {
                    sb.append("• Not eligible (STAM requires at least Maqbul)\n")
                }

                sb.append("\nOther notes:\n")
                sb.append("• STAM minimum requirement is Maqbul\n")
            }
        }

        return Pair(sb.toString(), eligible)
    }

    // -------------------------
    // FINAL POPUP
    // -------------------------

    private fun checkEligibility() {
        val q = spinnerQualification.selectedItem.toString()

        val (msg, eligible) =
            if (q == "SPM") checkSpmEligibility()
            else checkOtherEligibility(q)

        val builder = AlertDialog.Builder(this)
        builder.setTitle("Eligibility Result")

        // If eligible → show link + Apply Now button
        if (eligible > 0) {
            builder.setMessage(msg + "\nYou can apply here:\nhttps://gmi.vialing.com/oa/login")
            builder.setNeutralButton("Apply Now") { _, _ ->
                startActivity(Intent(Intent.ACTION_VIEW, Uri.parse("https://gmi.vialing.com/oa/login")))
            }
        } else {
            builder.setMessage(msg) // no link
        }

        builder.setPositiveButton("OK", null)
        builder.show()
    }
}
