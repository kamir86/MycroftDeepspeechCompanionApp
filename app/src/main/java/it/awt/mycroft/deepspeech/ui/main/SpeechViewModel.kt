package it.awt.mycroft.deepspeech.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.mozilla.speechlibrary.SpeechResultCallback
import com.mozilla.speechlibrary.SpeechService
import com.mozilla.speechlibrary.stt.STTResult
import it.awt.mycroft.deepspeech.domain.Utterance
import it.awt.mycroft.deepspeech.domain.UtteranceActor
import it.awt.mycroft.deepspeech.usecase.MycroftRequestUseCase
import it.awt.mycroft.deepspeech.usecase.SpeechRecognitionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SpeechViewModel(application: Application) : AndroidViewModel(application) {
    val spokenList: MutableLiveData<List<Utterance>> = MutableLiveData()
    val recording: MutableLiveData<Boolean> = MutableLiveData()
    val voiceActivity: MutableLiveData<Double> = MutableLiveData()
    fun startSpeechRecognition() {
        val mSpeechService = SpeechService(getApplication())

        GlobalScope.launch(Dispatchers.IO) {
            val speechListener = object : SpeechResultCallback {
                override fun onStartListen() {
                    recording.postValue(true)
                    voiceActivity.postValue(0.0)
                }

                override fun onMicActivity(fftsum: Double) {
                    voiceActivity.postValue(fftsum)
                }

                override fun onDecoding() {
                }

                override fun onSTTResult(result: STTResult?) {
                    recording.postValue(false)
                }

                override fun onNoVoice() {
                    recording.postValue(false)
                }

                override fun onError(@SpeechResultCallback.ErrorType errorType: Int, error: String?) {
                    recording.postValue(false)
                }
            }
            SpeechRecognitionUseCase().startSpeechRecognition(
                ctx = getApplication(),
                mSpeechService,
                speechListener
            ).consume(onSuccess = {
                Log.i("spoken", "spoken: "+it)
                if(!it.isBlank()){
                    spokenList.postValue(spokenList.value.orEmpty().toMutableList().apply {
                        this.add(Utterance(UtteranceActor.USER, it))
                    })
                }

            }, onError = {

            })

        }
    }

    fun sendRequest(requestPhrase: String) {
        val mycroftIp = PreferenceManager.getDefaultSharedPreferences(this.getApplication()).getString("mycroft_ip", null)
        if(mycroftIp.isNullOrBlank()){
            spokenList.postValue(spokenList.value.orEmpty().toMutableList().apply {
                this.add(Utterance(UtteranceActor.MYCROFT, "Impostare l'ip di mycroft sulle impostazioni"))
            })
        } else {
            GlobalScope.launch(Dispatchers.IO) {
                MycroftRequestUseCase().postRequest(mycroftIp!!, requestPhrase).consume(onSuccess = {
                    Log.i("response", "response: " + it)
                    spokenList.postValue(spokenList.value.orEmpty().toMutableList().apply {
                        this.add(Utterance(UtteranceActor.MYCROFT, it))
                    })
                }, onError = {

                })
            }
        }
    }
}