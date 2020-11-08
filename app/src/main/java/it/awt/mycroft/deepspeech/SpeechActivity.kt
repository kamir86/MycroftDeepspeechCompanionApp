package it.awt.mycroft.deepspeech

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.Menu
import android.view.MenuItem
import it.awt.mycroft.deepspeech.settings.SettingsActivity
import it.awt.mycroft.deepspeech.ui.main.SpeechFragment

class SpeechActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.speech_activity)
        if (savedInstanceState == null) {
            supportFragmentManager.beginTransaction()
                .replace(R.id.container, SpeechFragment.newInstance())
                .commitNow()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.action_settings -> {
                startSettingsActivity(); return true;
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun startSettingsActivity() {
        val intent = Intent(this, SettingsActivity::class.java);
        startActivity(intent)
    }
}