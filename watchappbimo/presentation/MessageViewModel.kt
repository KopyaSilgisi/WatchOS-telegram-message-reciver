package com.example.watchappbimo.presentation

import android.os.Handler
import android.os.Looper
import android.util.Log
import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.net.URL
import javax.net.ssl.HttpsURLConnection

private val botToken = "TelekgramBotToken**********************"

class MessageViewModel : ViewModel() {
    private val handler = Handler(Looper.getMainLooper())
    private val checkInterval = 5000L
    private val _messages = mutableStateListOf<String>()
    val messages: List<String> get() = _messages

    // Yönlendirme callback'i (ekrandan verilecek)
    var onNewMessage: (() -> Unit)? = null

    private val fetchRunnable = object : Runnable {
        override fun run() {
            fetchMessages()
            handler.postDelayed(this, checkInterval)
        }
    }

    init {
        handler.post(fetchRunnable)
    }

    private fun fetchMessages() {
        viewModelScope.launch(Dispatchers.IO) {
            val text = getLastMessageFromTelegram()
            if (text.isNotEmpty() && (_messages.isEmpty() || _messages.last() != text)) {
                _messages.add(text)

                // Ana thread'de yönlendirme tetikleniyor
                viewModelScope.launch(Dispatchers.Main) {
                    onNewMessage?.invoke()
                }
            }
        }
    }

    private fun getLastMessageFromTelegram(): String {
        val urlStr = "https://api.telegram.org/bot$botToken/getUpdates"

        return try {
            val connection = URL(urlStr).openConnection() as HttpsURLConnection
            connection.requestMethod = "GET"
            val response = connection.inputStream.bufferedReader().readText()
            connection.disconnect()

            Log.d("TelegramDebug", "Raw response: $response")

            val json = JSONObject(response)
            val results = json.getJSONArray("result")

            for (i in results.length() - 1 downTo 0) {
                val item = results.getJSONObject(i)
                val message = item.optJSONObject("message")
                val text = message?.optString("text", null)
                if (!text.isNullOrEmpty()) {
                    Log.d("TelegramDebug", "Found text: $text")
                    return text
                }
            }

            "no messages"
        } catch (e: Exception) {
            Log.e("TelegramDebug", "Exception: ${e.message}")
            "no messages"
        }
    }
}
