package it.awt.mycroft.deepspeech.usecase

import android.content.Context
import android.os.Environment
import android.util.Log
import com.mozilla.speechlibrary.SpeechService
import com.mozilla.speechlibrary.SpeechServiceSettings
import com.mozilla.speechlibrary.stt.STTResult
import com.mozilla.speechlibrary.utils.ModelUtils
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.nio.file.Files
import java.nio.file.Path
import java.nio.file.StandardCopyOption
import kotlin.coroutines.suspendCoroutine
import com.mozilla.speechlibrary.SpeechResultCallback
import java.lang.IllegalArgumentException
import kotlin.coroutines.resume


class SpeechRecognitionUseCase {
    private val TAG: String = "SpeechRecognitionUseCase"
    private val MODEL_PATH = "deepspeech/models/it/"

    suspend fun startSpeechRecognition(ctx : Context, mSpeechService : SpeechService) : Either<Throwable, String>{
        return suspendCoroutine { cont ->

            val mVoiceSearchListener = object : SpeechResultCallback{
                override fun onStartListen() {
                }

                override fun onMicActivity(fftsum: Double) {
                }

                override fun onDecoding() {
                }

                override fun onSTTResult(result: STTResult?) {
                    cont.resume(Either.Success(result?.mTranscription!!))
                }

                override fun onNoVoice() {
                }

                override fun onError(@SpeechResultCallback.ErrorType errorType: Int, error: String?) {
                    cont.resume(Either.Error(IllegalArgumentException(error)))
                }
            }
            val language = "it-it"
            val modelPath =
                ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absoluteFile?.toPath()
                    ?.resolve(MODEL_PATH)?.toString();
            val builder: SpeechServiceSettings.Builder = SpeechServiceSettings.Builder()
                .withLanguage(language)
                .withStoreSamples(true)
                .withStoreTranscriptions(true)
                .withProductTag("product-tag")
                .withUseDeepSpeech(true) // If using DeepSpeech
                .withModelPath(modelPath!!) // If using DeepSpeech

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
            val modelPath = ctx.getExternalFilesDir(Environment.DIRECTORY_DOWNLOADS)?.absoluteFile?.toPath()?.resolve(MODEL_PATH)
            Files.createDirectories(modelPath)
            ctx.assets.open("model/0.8.2/output_graph.tflite").use {
                val path = modelPath?.resolve("output_graph.tflite")
                Log.i(TAG, "copying file to: "+path?.toString())
                Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING);
            }
            ctx.assets.open("model/0.8.2/alphabet.txt").use {
                val path = modelPath?.resolve("alphabet.txt")
                Log.i(TAG, "copying file to: "+path?.toString())
                Files.createDirectories(path?.parent)
                Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING);
            }
            ctx.assets.open("model/0.8.2/scorer").use {
                val path = modelPath?.resolve("scorer")
                Log.i(TAG, "copying file to: "+path?.toString())
                Files.createDirectories(path?.parent)
                Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING);
            }
            ctx.assets.open("model/0.8.2/info.json").use {
                val path = modelPath?.resolve("info.json")
                Log.i(TAG, "copying file to: "+path?.toString())
                Files.createDirectories(path?.parent)
                Files.copy(it, path, StandardCopyOption.REPLACE_EXISTING);
            }
        }

    }
}
