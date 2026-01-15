package com.example.phishing_detector_mobile

import android.content.Context
import android.util.Log
import ai.onnxruntime.OrtEnvironment
import ai.onnxruntime.OrtSession
import ai.onnxruntime.OnnxTensor
import java.nio.LongBuffer
import java.util.Collections

class PhishingClassifier(context: Context) {

    private var ortEnvironment: OrtEnvironment? = null
    private var ortSession: OrtSession? = null
    var lastError: String = ""

    init {
        try {
            ortEnvironment = OrtEnvironment.getEnvironment()
            // Model must be in outputs/assets/phishing_classifier.onnx
            val modelBytes = context.assets.open("phishing_classifier.onnx").readBytes()
            ortSession = ortEnvironment?.createSession(modelBytes)
            Log.d("PhishingClassifier", "ONNX Model loaded successfully")
        } catch (e: Exception) {
            lastError = "Init failed: ${e.message}"
            Log.e("PhishingClassifier", "Error initializing ONNX session: " + e.message)
            e.printStackTrace()
        }
    }

    fun classify(url: String): Float {
        if (ortSession == null || ortEnvironment == null) {
            val msg = if (lastError.isNotEmpty()) lastError else "Session not initialized. Model likely didn't load."
            lastError = msg
            Log.e("PhishingClassifier", lastError)
            return -1f
        }
        lastError = ""

        try {
            // Prepare input tensor
            // Specifically for sklearn-onnx converted models which usually take a tensor of shape [N, 1] string
            val inputs = Array(1) { arrayOf(url) }
            // Dimensions for [1, 1]
            val tensor = OnnxTensor.createTensor(ortEnvironment, inputs)
            
            val inputName = ortSession!!.inputNames.iterator().next()
            val inputsMap = Collections.singletonMap(inputName, tensor)

            // Run inference
            val results = ortSession!!.run(inputsMap)
            
            // Output handling depends on how the model was exported.
            // Scikit-learn classifiers usually export:
            // "output_label" -> Array of predicted labels
            // "output_probability" -> Sequence of Map<Label, Probability>
            
            // Let's assume we want the probability of the positive class (1).
            // Usually the probability output is the second output.
            // Output handling
            val outputProb = results.get(1) // Second output is probabilities
            
            // The value is a List containing OnnxMap objects
            val sequence = outputProb.value as List<*>
            
            // Get the first element (for the first input) and cast to OnnxMap
            val mapOrValue = sequence[0]
            
            val phishingProb: Float = if (mapOrValue is ai.onnxruntime.OnnxMap) {
                 @Suppress("UNCHECKED_CAST")
                 val map = mapOrValue.value as Map<Long, Float>
                 map[1L] ?: 0f
            } else {
                 // Fallback if structure is different (e.g. just a Map directly? Unlikely for Sequence)
                 Log.w("PhishingClassifier", "Unexpected output type: ${mapOrValue?.javaClass?.name}")
                 0f
            }
            
            tensor.close()
            results.close()
            
            return phishingProb

        } catch (e: Exception) {
            lastError = e.message ?: "Unknown Inference Error"
            Log.e("PhishingClassifier", "Inference failed: " + lastError)
            e.printStackTrace()
            return -1f
        }
    }

    fun close() {
        ortSession?.close()
        ortEnvironment?.close()
    }
}
