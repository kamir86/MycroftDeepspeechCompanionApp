package it.awt.mycroft.deepspeech.ui.main

import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.recyclerview.widget.RecyclerView
import it.awt.mycroft.deepspeech.R


class UtteranceAdapter(private val newList: List<Pair<String, String>>) : RecyclerView.Adapter<UtteranceAdapter.UtteranceViewHolder>() {
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
        if(newList[position].first.equals("Me")) {
            ll.gravity = Gravity.END;
        }else{
            ll.gravity = Gravity.START;
        }
        ll.requestLayout()
        textView.apply {
            if(newList[position].first.equals("Me")){
                applyUserStyle(textView);
            }else{
                applyMycroftStyle(textView);
            }
            text = newList[position].second
        }
    }

    private fun applyMycroftStyle(textView: TextView?) {
        textView?.setBackgroundColor(textView.resources.getColor(R.color.mycroft_color))
        textView?.gravity = Gravity.START
    }

    private fun applyUserStyle(textView: TextView?) {
        textView?.setBackgroundColor(textView.resources.getColor(R.color.user_color))
        textView?.gravity = Gravity.END
    }

    override fun getItemCount(): Int {
        return newList.size;
    }

}
