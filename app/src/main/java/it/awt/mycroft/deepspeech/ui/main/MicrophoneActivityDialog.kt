package it.awt.mycroft.deepspeech.ui.main

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.animation.LinearInterpolator
import androidx.fragment.app.DialogFragment
import it.awt.mycroft.deepspeech.R
import kotlin.math.abs

class MicrophoneActivityDialog : DialogFragment() {

    private val AUDIO_THRESHOLD = 100.0
    private var animating: Boolean = false
    private var externalShape: View? = null
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val v = inflater.inflate(R.layout.microphone_input_fragment, container, false)
        externalShape = v.findViewById(R.id.external_shape)
        externalShape?.alpha = 0f
        return v
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)
        externalShape?.alpha = 0f
    }

    fun pulse(d: Double) {
        if(abs(d) - AUDIO_THRESHOLD <= 0){
            return
        }
        if (animating) {
            return
        }
        externalShape?.apply {
            animating = true
            this.alpha = 0f
            this.animate().withEndAction {
                animating = false
            }.apply {
                interpolator = LinearInterpolator()
                duration = 1000
                alpha(1.0f)
                startDelay = 0
                start()
            }
        }
    }
}