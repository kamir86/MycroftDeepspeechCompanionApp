package it.awt.mycroft.deepspeech.ui.main

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import it.awt.mycroft.deepspeech.R
import it.awt.mycroft.deepspeech.domain.Utterance
import it.awt.mycroft.deepspeech.domain.UtteranceActor


class UtteranceAdapter(private val newList: List<Utterance>) : RecyclerView.Adapter<UtteranceAdapter.UtteranceViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): UtteranceViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.utterance_fragment, parent, false)
        return UtteranceViewHolder(view)
    }

    class UtteranceViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
    }

    override fun onBindViewHolder(holder: UtteranceViewHolder, position: Int) {
        val textView = holder.itemView.findViewById<TextView>(R.id.utterance_text)
        val ll = holder.itemView as LinearLayout;
        if(newList[position].actor == UtteranceActor.USER) {
            ll.gravity = Gravity.END;
        }else{
            ll.gravity = Gravity.START;
        }
        ll.requestLayout()
        textView.apply {
            if(newList[position].actor == UtteranceActor.USER){
                applyUserStyle(textView);
            }else{
                applyMycroftStyle(textView);
            }
            text = newList[position].text
        }
    }

    private fun applyMycroftStyle(textView: TextView?) {
        textView?.apply {
            background = resources.getDrawable(R.drawable.incoming_message_bg)
            val scale = resources.displayMetrics.density
            val voicePadding = (20 * scale + 0.5f).toInt()
            val regularPadding = (5 * scale + 0.5f).toInt()
            gravity = Gravity.START
            setPadding(voicePadding, regularPadding, regularPadding, regularPadding)
        }
    }

    private fun applyUserStyle(textView: TextView?) {
        textView?.apply {
            background = resources.getDrawable(R.drawable.outgoing_message_bg)
            val scale = resources.displayMetrics.density
            val voicePadding = (20 * scale + 0.5f).toInt()
            val regularPadding = (5 * scale + 0.5f).toInt()
            gravity = Gravity.END
            setPadding(regularPadding, regularPadding, voicePadding, regularPadding)
        }
    }

    override fun getItemCount(): Int {
        return newList.size;
    }

}
