package it.awt.mycroft.deepspeech.usecase

import android.util.Log
import org.java_websocket.client.WebSocketClient
import org.java_websocket.handshake.ServerHandshake
import org.json.JSONException
import org.json.JSONObject
import java.net.URI
import java.net.URISyntaxException
import java.util.function.Consumer
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine

class MycroftRequestUseCase {
    private val TAG: String? = "MycroftRequestUseCase"
    private var webSocketClient: WebSocketClient? = null


    suspend fun postRequest(mycroftIp: String, requestPhrase: String): Either<Throwable, String> {
        return suspendCoroutine { cont ->
            sendMessage(mycroftIp, requestPhrase) {
                it?.run {
                    cont.resume(Either.Success(this))
                }
            }
        }

    }


    private fun connectWebSocket(mycroftIp: String, callback: Consumer<String?>) {
        val uri = buildURI(mycroftIp)
        uri?.run {
            webSocketClient = object : WebSocketClient(this) {
                override fun onOpen(serverHandshake: ServerHandshake) {
                    Log.i(TAG, "Opened")
                }

                override fun onMessage(message: String) {
                    Log.i(TAG, message)
                    try {
                        val obj = JSONObject(message)
                        if (obj.optString("type") == "speak") {
                            val ret = obj.optJSONObject("data")?.optString("utterance")
                            callback.accept(ret)
                        }
                    } catch (e: JSONException) {
                        Log.e(
                            TAG,
                            "The response received did not conform to our expected JSON format.",
                            e
                        )
                    }
                }

                override fun onClose(i: Int, s: String, b: Boolean) {
                    Log.i(TAG, "Closed $s")

                }

                override fun onError(e: Exception) {
                    Log.i(TAG, "Error " + e.message)
                }
            }
            webSocketClient?.connectBlocking()
        }
    }

    private fun sendMessage(mycroftIp: String, msg: String, callback: Consumer<String?>) {
        val json = "{\"data\": {\"utterances\": [\"$msg\"]}, \"type\": \"recognizer_loop:utterance\", \"context\": null}"
        if(webSocketClient == null || webSocketClient!!.connection.isClosed){
                connectWebSocket(mycroftIp, callback)
        }
        webSocketClient?.send(json)

    }

    private fun buildURI(mycroftIp: String): URI? {
        return mycroftIp.run {
            try {
                URI("ws://$this:8181/core")
            } catch (e: URISyntaxException) {
                Log.e("WS", "Unable to build URI for websocket", e)
                null
            }
        }
    }

}
