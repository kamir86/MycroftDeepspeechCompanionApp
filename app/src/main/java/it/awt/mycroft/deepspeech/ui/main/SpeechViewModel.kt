package it.awt.mycroft.deepspeech.ui.main

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.MutableLiveData
import androidx.preference.PreferenceManager
import com.mozilla.speechlibrary.SpeechService
import it.awt.mycroft.deepspeech.usecase.MycroftRequestUseCase
import it.awt.mycroft.deepspeech.usecase.SpeechRecognitionUseCase
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch

class SpeechViewModel(application: Application) : AndroidViewModel(application) {
    val spokenList: MutableLiveData<List<Pair<String, String>>> = MutableLiveData()
    fun startSpeechRecognition() {
        val mSpeechService = SpeechService(getApplication())

        GlobalScope.launch(Dispatchers.IO) {
            SpeechRecognitionUseCase().startSpeechRecognition(
                ctx = getApplication(),
                mSpeechService
            ).consume(onSuccess = {
                Log.i("spoken", "spoken: "+it)
                spokenList.postValue(spokenList.value.orEmpty().toMutableList().apply {
                    this.add(Pair("Me", it))
                })
            }, onError = {

            })

        }
    }

    fun sendRequest(requestPhrase: String) {
        val mycroftIp = PreferenceManager.getDefaultSharedPreferences(this.getApplication()).getString("mycroft_ip", null)
        if(mycroftIp == null){
            spokenList.postValue(spokenList.value.orEmpty().toMutableList().apply {
                this.add(Pair("Mycroft", "Impostare l'ip di mycroft sulle impostazioni"))
            })
        }
        GlobalScope.launch(Dispatchers.IO) {
            MycroftRequestUseCase().postRequest(mycroftIp!!, requestPhrase).consume(onSuccess = {
                Log.i("response", "response: " + it)
                spokenList.postValue(spokenList.value.orEmpty().toMutableList().apply {
                    this.add(Pair("Mycroft", it))
                })
            }, onError = {

            })
        }
    }
}