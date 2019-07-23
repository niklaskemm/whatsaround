package lbs.whatsaround

import android.app.Activity
import android.content.Context
import android.speech.tts.TextToSpeech
import android.speech.tts.UtteranceProgressListener
import android.widget.Toast
import java.util.*

class TTS(private val activity: Context?,
          private val message: String?,
          private val action: String?) : TextToSpeech.OnInitListener {

    var tts: TextToSpeech = TextToSpeech(activity, this)

    override fun onInit(i: Int) {

        if (action == "Speak") {
            if (i == TextToSpeech.SUCCESS) {

                val locale = Locale.GERMAN

                val result: Int
                result = tts.setLanguage(locale)

                if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                    Toast.makeText(activity, "This Language is not supported", Toast.LENGTH_SHORT).show()
                } else {
                    speakOut(message)
                }

            } else {
                Toast.makeText(activity, "Initilization Failed!", Toast.LENGTH_SHORT).show()
            }
        }
        else {
            stopSpeaking(activity)
        }
    }

    fun speakOut(message: String?) {
        tts.speak(message, TextToSpeech.QUEUE_FLUSH, null, null)

    }

    // Not working yet. Don't know why.
    fun stopSpeaking(activity: Context?) {
        if (tts.isSpeaking) {
            Toast.makeText(activity, "Stopped speaking", Toast.LENGTH_SHORT).show()
            tts.stop()
            tts.shutdown()
        }

        else {
            // if not speaking
            Toast.makeText(activity, "Not speaking", Toast.LENGTH_SHORT).show()
        }
    }


}
