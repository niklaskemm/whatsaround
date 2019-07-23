package lbs.whatsaround

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import lbs.whatsaround.TTS
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btn_changeActivity.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            startActivity(intent)
        }

        mInstance = this

    }

    // Needed to get Context in TTS Class
    companion object {
        lateinit var mInstance: MainActivity
        fun getContext(): Context? {
            return mInstance.applicationContext
        }
    }

}

