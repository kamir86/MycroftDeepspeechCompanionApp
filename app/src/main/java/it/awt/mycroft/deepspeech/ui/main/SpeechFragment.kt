package it.awt.mycroft.deepspeech.ui.main

import android.Manifest
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Button
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.github.florent37.runtimepermission.kotlin.askPermission
import it.awt.mycroft.deepspeech.R


class SpeechFragment : Fragment() {

    companion object {
        fun newInstance() = SpeechFragment()
    }

    private lateinit var viewModel: SpeechViewModel

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        val v = inflater.inflate(R.layout.speech_fragment, container, false)
        v.findViewById<Button>(R.id.button).setOnClickListener {
            startSpeechRecognition(it)
        }
        return v
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        viewModel = ViewModelProviders.of(this).get(SpeechViewModel::class.java)
        viewModel.spokenList.observe(viewLifecycleOwner, {
            refreshSpokenList(it)
            if (it.last().first.equals("Me")) {
                viewModel.sendRequest(it.last().second);
            }
        })
    }

    private fun refreshSpokenList(newList: List<Pair<String, String>>) {
        Log.i("tag", "newList: " + newList.toString())
        val recyclerView = view?.findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView?.apply {
            layoutManager = LinearLayoutManager(context).apply {
                stackFromEnd = true
            }
            adapter = makeAdapter(newList)
        }
    }

    private fun makeAdapter(newList: List<Pair<String, String>>): UtteranceAdapter {
        return UtteranceAdapter(newList)
    }

    fun startSpeechRecognition(view: View){
        askPermission(
            Manifest.permission.RECORD_AUDIO,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE
        ){
            startSpeechRecognition()
        }.onDeclined { e ->
            //at least one permission have been declined by the user
        }
    }

    fun startSpeechRecognition(){
        viewModel.startSpeechRecognition();
    }
}