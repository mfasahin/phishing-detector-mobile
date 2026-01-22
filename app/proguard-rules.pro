# Add project specific ProGuard rules here.
# You can control the set of applied configuration files using the
# proguardFiles setting in build.gradle.
#
# For more details, see
#   http://developer.android.com/guide/developing/tools/proguard.html

# If your project uses WebView with JS, uncomment the following
# and specify the fully qualified class name to the JavaScript interface
# class:
#-keepclassmembers class fqcn.of.javascript.interface.for.webview {
#   public *;
#}

# Uncomment this to preserve the line number information for
# debugging stack traces.
-keepattributes SourceFile,LineNumberTable

# If you keep the line number information, uncomment this to
# hide the original source file name.
#-renamesourcefileattribute SourceFile

# --- ONNX Runtime Rules ---
# Prevent obfuscation of ONNX Runtime native methods and JNI interfaces
-keep class ai.onnxruntime.** { *; }

# --- Gson / Serialization Rules ---
# If using Gson or other reflection-based serialization, keep data classes
# Ensure your data classes (if any) are kept. Example:
# -keep class com.example.phishing_detector_mobile.data.** { *; }

# --- Android Components ---
# Ensure MainActivity and other components referenced in Manifest are kept
-keep public class * extends android.app.Activity
-keep public class * extends android.app.Application
-keep public class * extends android.app.Service
-keep public class * extends android.content.BroadcastReceiver
-keep public class * extends android.content.ContentProvider

# --- Kotlin Coroutines (Optional but recommended) ---
-keepnames class kotlinx.coroutines.internal.MainDispatcherFactory {}
-keepnames class kotlinx.coroutines.CoroutineExceptionHandler {}
-keepclassmembers class kotlinx.coroutines.android.AndroidExceptionPreHandler {
    <init>();
}