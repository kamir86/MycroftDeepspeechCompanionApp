package it.awt.mycroft.deepspeech.usecase

import android.content.Context
import android.util.Log
import com.mozilla.speechlibrary.SpeechResultCallback
import com.mozilla.speechlibrary.SpeechService
import com.mozilla.speechlibrary.SpeechServiceSettings
import com.mozilla.speechlibrary.stt.STTResult
import com.mozilla.speechlibrary.utils.ModelUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import kotlin.coroutines.resume
import kotlin.coroutines.suspendCoroutine


class SpeechRecognitionUseCase {

    suspend fun startSpeechRecognition(ctx : Context, mSpeechService : SpeechService, voiceListener : SpeechResultCallback?) : Either<Throwable, String>{
        return suspendCoroutine { cont ->

            val mVoiceSearchListener = object : SpeechResultCallback{
                override fun onStartListen() {
                    voiceListener?.onStartListen()
                }

                override fun onMicActivity(fftsum: Double) {
                    voiceListener?.onMicActivity(fftsum)
                }

                override fun onDecoding() {
                    voiceListener?.onDecoding()
                }

                override fun onSTTResult(result: STTResult?) {
                    cont.resume(Either.Success(result?.mTranscription!!))
                    voiceListener?.onSTTResult(result)
                }

                override fun onNoVoice() {
                    voiceListener?.onNoVoice()
                }

                override fun onError(@SpeechResultCallback.ErrorType errorType: Int, error: String?) {
                    cont.resume(Either.Error(IllegalArgumentException(error)))
                    voiceListener?.onError(errorType, error)
                }
            }

            val modelPath = File(ctx.filesDir, MODEL_PATH).absolutePath
            val builder: SpeechServiceSettings.Builder = SpeechServiceSettings.Builder()
                .withLanguage(LANGUAGE)
                .withStoreSamples(true)
                .withStoreTranscriptions(true)
                .withProductTag("product-tag")
                .withUseDeepSpeech(true) // If using DeepSpeech
                .withModelPath(modelPath) // If using DeepSpeech

            if (ModelUtils.isReady(modelPath)) {
                // The model is already downloaded and unzipped
                mSpeechService.start(builder.build(), mVoiceSearchListener)
            } else {
                GlobalScope.launch(Dispatchers.Main) {
                    copyModel(ctx)
                    mSpeechService.start(builder.build(), mVoiceSearchListener)
                }
            }
        }
    }

    private suspend fun copyModel(ctx: Context) {
        return withContext(Dispatchers.IO) {
            val modelPath = File(ctx.filesDir, MODEL_PATH)
            modelPath.mkdirs()

            copy(ctx, modelPath, "output_graph.tflite")
            copy(ctx, modelPath, "alphabet.txt")
            copy(ctx, modelPath, "scorer")
            copy(ctx, modelPath, "info.json")
        }
    }

    @Throws(IOException::class)
    private fun copy(ctx: Context, modelPath: File, filename: String) {
        ctx.assets.open("model/0.8.2/$filename").use { it ->
            FileOutputStream(File(modelPath, filename)).use { output ->
                val buffer = ByteArray(1024)
                var length: Int
                while (it.read(buffer).also { length = it } > 0) {
                    output.write(buffer, 0, length)
                }
            }
        }
    }

    companion object {
        private const val TAG = "SpeechRecognitionUseCase"
        private const val MODEL_PATH = "deepspeech/models/it/"
        private const val LANGUAGE = "it-it"
    }
}
