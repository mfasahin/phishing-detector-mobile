package com.example.phishing_detector_mobile

import android.graphics.Color
import android.os.Bundle
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.button.MaterialButton
import com.google.android.material.textfield.TextInputEditText

class MainActivity : AppCompatActivity() {

    private lateinit var classifier: PhishingClassifier
    private lateinit var urlEditText: TextInputEditText
    private lateinit var checkButton: MaterialButton
    private lateinit var resultTextView: TextView
    private lateinit var resultCardView: androidx.cardview.widget.CardView
    private lateinit var gaugeView: GaugeView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // Initialize Classifier
        classifier = PhishingClassifier(this)

        // Bind Views
        urlEditText = findViewById(R.id.urlEditText)
        checkButton = findViewById(R.id.checkButton)
        resultTextView = findViewById(R.id.resultTextView)
        resultCardView = findViewById(R.id.resultCardView)
        gaugeView = findViewById(R.id.gaugeView)

        checkButton.setOnClickListener {
            val url = urlEditText.text.toString().trim()
            if (url.isNotEmpty()) {
                analyzeUrl(url)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun analyzeUrl(url: String) {
        val score = classifier.classify(url)
        
        // Determine result based on a threshold (e.g., 0.5)
        // If score is high (close to 1.0), it's likely phishing.
        // If score is low (close to 0.0), it's likely safe.
        // Note: Adjust the threshold and logic based on your specific model output.

        if (score == -1f) {
             resultTextView.text = "Error: ${classifier.lastError}"
             resultTextView.textSize = 16f
             resultTextView.setTextColor(Color.WHITE)
             return
        }

        val isPhishing = score > 0.5f
        
            if (isPhishing) {
            resultTextView.text = "⚠️ WARNING: PHISHING DETECTED!"
            resultTextView.setTextColor(Color.parseColor("#FF5252")) // Neon Red
        } else {
            resultTextView.text = "✅ URL Appears Safe"
            resultTextView.setTextColor(Color.parseColor("#00E676")) // Neon Green
        }
        
        // Update Gauge
        gaugeView.setScore(score)
        
        // Optimize visibility and animation
        resultCardView.visibility = android.view.View.VISIBLE
        resultCardView.alpha = 0f
        resultCardView.scaleX = 0.8f
        resultCardView.scaleY = 0.8f
        
        resultCardView.animate()
            .alpha(1f)
            .scaleX(1f)
            .scaleY(1f)
            .setDuration(500)
            .setInterpolator(android.view.animation.OvershootInterpolator())
            .start()
    }

    override fun onDestroy() {
        super.onDestroy()
        classifier.close()
    }
}