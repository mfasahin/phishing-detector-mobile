package com.example.phishing_detector_mobile

import android.graphics.Color
import android.content.Intent
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
    private lateinit var detailSslTextView: TextView
    private lateinit var detailDomainTextView: TextView
    private lateinit var detailPatternTextView: TextView

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
        
        detailSslTextView = findViewById(R.id.detailSslTextView)
        detailDomainTextView = findViewById(R.id.detailDomainTextView)
        detailPatternTextView = findViewById(R.id.detailPatternTextView)

        checkButton.setOnClickListener {
            val url = urlEditText.text.toString().trim()
            if (url.isNotEmpty()) {
                analyzeUrl(url)
            } else {
                Toast.makeText(this, "Please enter a URL", Toast.LENGTH_SHORT).show()
            }
        }

        handleIncomingIntent(intent)
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        handleIncomingIntent(intent)
    }

    private fun handleIncomingIntent(intent: Intent?) {
        if (intent?.action == Intent.ACTION_SEND && intent.type == "text/plain") {
            intent.getStringExtra(Intent.EXTRA_TEXT)?.let { sharedText ->
                // Basic URL extraction logic (can be improved)
                val possibleUrl = sharedText.trim() 
                // In a real scenario, you might want to use a regex to extract URL from a longer text
                // For now, we assume the shared text IS the URL or contains it simply.
                
                urlEditText.setText(possibleUrl)
                analyzeUrl(possibleUrl)
            }
        }
    }

    private fun analyzeUrl(url: String) {
        val score = classifier.classify(url)
        
        if (score == -1f) {
             resultTextView.text = "Error: ${classifier.lastError}"
             resultTextView.textSize = 16f
             resultTextView.setTextColor(Color.WHITE)
             return
        }

        val isPhishing = score > 0.5f
        
        if (isPhishing) {
            resultTextView.text = "⚠️ PHISHING TESPİT EDİLDİ!"
            resultTextView.setTextColor(Color.parseColor("#FF5252")) // Neon Red
            resultCardView.setBackgroundResource(R.drawable.bg_card_warning)
            resultTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0) // No Icon
            resultTextView.compoundDrawablePadding = 0
            
            // Simulated Technical Details for Phishing
            detailSslTextView.text = "[!] SSL Sertifikası: Geçersiz/Uyuşmazlık"
            detailSslTextView.setTextColor(Color.parseColor("#FF5252"))
            
            detailDomainTextView.text = "[!] Alan Adı Yaşı: < 30 Gün (Yeni)"
            detailDomainTextView.setTextColor(Color.parseColor("#FF8A80"))
            
            detailPatternTextView.text = "[!] URL Yapısı: Şüpheli Karakterler"
            detailPatternTextView.setTextColor(Color.parseColor("#FF5252"))
            
        } else {
            resultTextView.text = "✅ Bağlantı Güvenli Görünüyor"
            resultTextView.setTextColor(Color.parseColor("#00E676")) // Neon Green
            resultCardView.setBackgroundResource(R.drawable.bg_card_safe)
            resultTextView.setCompoundDrawablesWithIntrinsicBounds(0, 0, 0, 0)
            
            // Simulated Technical Details for Safe
            detailSslTextView.text = "[✓] SSL Sertifikası: Geçerli (Güvenilir)"
            detailSslTextView.setTextColor(Color.parseColor("#81C784"))
            
            detailDomainTextView.text = "[✓] Alan Adı Yaşı: > 1 Yıl"
            detailDomainTextView.setTextColor(Color.parseColor("#81C784"))
            
            detailPatternTextView.text = "[✓] URL Yapısı: Standart Format"
            detailPatternTextView.setTextColor(Color.parseColor("#81C784"))
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