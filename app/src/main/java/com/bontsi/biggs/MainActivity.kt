package com.bontsi.biggs

import android.Manifest
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.speech.RecognizerIntent
import android.speech.tts.TextToSpeech
import android.widget.Button
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity(), TextToSpeech.OnInitListener {
    private lateinit var tts: TextToSpeech
    private lateinit var statusText: TextView
    private lateinit var commandText: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        statusText = findViewById(R.id.statusText)
        commandText = findViewById(R.id.commandText)
        val btnTalk: Button = findViewById(R.id.btnTalk)
        tts = TextToSpeech(this, this)
        ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.RECORD_AUDIO, Manifest.permission.CALL_PHONE, Manifest.permission.READ_CONTACTS), 101)
        btnTalk.setOnClickListener { startListening() }
        speak("Hey, I'm Biggs. Tap to talk to me.")
    }

    private fun startListening() {
        val intent = Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH).apply {
            putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM)
            putExtra(RecognizerIntent.EXTRA_PROMPT, "Biggs is listening...")
        }
        startActivityForResult(intent, 100)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == 100 && resultCode == RESULT_OK) {
            val cmd = data?.getStringArrayListExtra(RecognizerIntent.EXTRA_RESULTS)?.get(0) ?: ""
            commandText.text = "You said: $cmd"
            handle(cmd.lowercase())
        }
    }

    private fun handle(cmd: String) {
        when {
            cmd.contains("call") -> {
                val num = cmd.replace("call", "").trim()
                speak("Calling $num")
                startActivity(Intent(Intent.ACTION_CALL, Uri.parse("tel:$num")))
            }
            cmd.contains("whatsapp") -> { speak("Opening WhatsApp"); startActivity(packageManager.getLaunchIntentForPackage("com.whatsapp")) }
            cmd.contains("youtube") -> { speak("Opening YouTube"); startActivity(packageManager.getLaunchIntentForPackage("com.google.android.youtube")) }
            cmd.contains("camera") -> { speak("Opening camera"); startActivity(Intent(MediaStore.ACTION_IMAGE_CAPTURE)) }
            cmd.contains("settings") -> { speak("Opening settings"); startActivity(Intent(Settings.ACTION_SETTINGS)) }
            cmd.contains("time") -> {
                val time = SimpleDateFormat("HH:mm", Locale.getDefault()).format(Date())
                speak("It's $time"); statusText.text = time
            }
            cmd.contains("battery") -> {
                val bm = getSystemService(BATTERY_SERVICE) as android.os.BatteryManager
                val pct = bm.getIntProperty(android.os.BatteryManager.BATTERY_PROPERTY_CAPACITY)
                speak("Battery is $pct percent")
            }
            cmd.contains("search") -> {
                val q = cmd.replace("search", "").trim()
                speak("Searching for $q")
                startActivity(Intent(Intent.ACTION_WEB_SEARCH).putExtra("query", q))
            }
            else -> {
                speak("Searching Google for $cmd")
                startActivity(Intent(Intent.ACTION_WEB_SEARCH).putExtra("query", cmd))
            }
        }
    }

    private fun speak(t: String) { statusText.text = t; tts.speak(t, TextToSpeech.QUEUE_FLUSH, null, null) }
    override fun onInit(status: Int) { if (status == TextToSpeech.SUCCESS) tts.language = Locale.getDefault() }
}
