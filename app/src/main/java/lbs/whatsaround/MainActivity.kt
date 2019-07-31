package lbs.whatsaround

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import kotlinx.android.synthetic.main.activity_main.*
import kotlinx.android.synthetic.main.popup.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val popupText_radius = "The radius defines the distance around your current location from which the Wikipedia articles will be fetched. The greater the radius, the longer the search will take."
        val popupTitle_radius = "Radius"

        val popupText_limit = "You can define the maximum number of results here. Remember that the actual number of results also depends on the radius."
        val popupTitle_limit = "Max no. of results"

        val popupText_interval = "The update interval defines the amount of reloading the results based on your change in location: \n\n" +
                "low: Update interval 5 minutes, smallest displacement 100m\n" +
                "normal: Update interval every minute, smallest displacement 50m\n" +
                "high: Update interval 10 seconds, smallest displacement 10m\n\n" +
                "The lower the update interval the lower the data usage."
        val popupTitle_interval = "Update Interval"

        val y_offset = 250

        sb_radius.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 0){
                    tv_radius.text = "100"
                }
                if (progress == 1){
                    tv_radius.text = "250"
                }
                if (progress == 2){
                    tv_radius.text = "500"
                }
                if (progress == 3){
                    tv_radius.text = "1000"
                }
                if (progress == 4){
                    tv_radius.text = "5000"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        sb_limit.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 0){
                    tv_limit.text = "5"
                }
                if (progress == 1){
                    tv_limit.text = "10"
                }
                if (progress == 2){
                    tv_limit.text = "25"
                }
                if (progress == 3){
                    tv_limit.text = "50"
                }
                if (progress == 4){
                    tv_limit.text = "100"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        sb_updateInterval.setOnSeekBarChangeListener(object: SeekBar.OnSeekBarChangeListener{
            override fun onProgressChanged(seekBar: SeekBar?, progress: Int, fromUser: Boolean) {
                if (progress == 0){
                    tv_updateInterval.text = "low"
                }
                if (progress == 1){
                    tv_updateInterval.text = "normal"
                }
                if (progress == 2) {
                    tv_updateInterval.text = "high"
                }
            }
            override fun onStartTrackingTouch(seekBar: SeekBar?) {
            }

            override fun onStopTrackingTouch(seekBar: SeekBar?) {
            }

        })

        btn_changeActivity.setOnClickListener {
            val intent = Intent(this, SecondActivity::class.java)
            intent.putExtra("radius" , tv_radius.text)
            intent.putExtra("limit", tv_limit.text)
            if (sb_updateInterval.progress == 0){
                intent.putExtra("interval", "300000")
                intent.putExtra("smallestDisp", "100.0")
            }
            if (sb_updateInterval.progress == 1){
                intent.putExtra("interval", "60000")
                intent.putExtra("smallestDisp", "50.0")
            }
            if (sb_updateInterval.progress == 2) {
                intent.putExtra("interval", "10000")
                intent.putExtra("smallestDisp", "10.0")
            }
            startActivity(intent)
        }

        iv_radiusInfo.setOnClickListener {
            // Initialize a new layout inflater instance
            val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.popup, null)

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                ConstraintLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                ConstraintLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            val btnClose = view.findViewById<ImageView>(R.id.iv_popupClose)

            val popupText = view.findViewById<TextView>(R.id.tv_popup)
            val popupTitle = view.findViewById<TextView>(R.id.tv_popupTitle)

            // If API level 23 or higher then execute the code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Create a new slide animation for popup window enter transition
                val slideIn = Fade()
                slideIn.duration = 500 //ms
                popupWindow.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Fade()
                //slideOut.slideEdge = Gravity.BOTTOM
                popupWindow.exitTransition = slideOut
            }

            btnClose.setOnClickListener{
                popupWindow.dismiss()
            }

            popupText.text = popupText_radius
            popupTitle.text = popupTitle_radius

            TransitionManager.beginDelayedTransition(activity_main)
            popupWindow.showAtLocation(
                activity_main, // Location to display popup window
                Gravity.BOTTOM, // Exact position of layout to display popup
                0, // X offset
                y_offset // Y offset
            )
        }

        iv_limitInfo.setOnClickListener {
            // Initialize a new layout inflater instance
            val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.popup, null)

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                ConstraintLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                ConstraintLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            val btnClose = view.findViewById<ImageView>(R.id.iv_popupClose)

            val popupText = view.findViewById<TextView>(R.id.tv_popup)
            val popupTitle = view.findViewById<TextView>(R.id.tv_popupTitle)

            // If API level 23 or higher then execute the code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Create a new slide animation for popup window enter transition
                val slideIn = Fade()
                slideIn.duration = 500 //ms
                popupWindow.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Fade()
                //slideOut.slideEdge = Gravity.BOTTOM
                popupWindow.exitTransition = slideOut
            }

            btnClose.setOnClickListener{
                popupWindow.dismiss()
            }

            popupText.text = popupText_limit
            popupTitle.text = popupTitle_limit

            TransitionManager.beginDelayedTransition(activity_main)
            popupWindow.showAtLocation(
                activity_main, // Location to display popup window
                Gravity.BOTTOM, // Exact position of layout to display popup
                0, // X offset
                y_offset // Y offset
            )
        }

        iv_updateInfo.setOnClickListener {
            // Initialize a new layout inflater instance
            val inflater: LayoutInflater = getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater

            // Inflate a custom view using layout inflater
            val view = inflater.inflate(R.layout.popup, null)

            // Initialize a new instance of popup window
            val popupWindow = PopupWindow(
                view, // Custom view to show in popup window
                ConstraintLayout.LayoutParams.WRAP_CONTENT, // Width of popup window
                ConstraintLayout.LayoutParams.WRAP_CONTENT // Window height
            )

            val btnClose = view.findViewById<ImageView>(R.id.iv_popupClose)

            val popupText = view.findViewById<TextView>(R.id.tv_popup)
            val popupTitle = view.findViewById<TextView>(R.id.tv_popupTitle)

            // If API level 23 or higher then execute the code
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                // Create a new slide animation for popup window enter transition
                val slideIn = Fade()
                slideIn.duration = 500 //ms
                popupWindow.enterTransition = slideIn

                // Slide animation for popup window exit transition
                val slideOut = Fade()
                //slideOut.slideEdge = Gravity.BOTTOM
                popupWindow.exitTransition = slideOut
            }

            btnClose.setOnClickListener{
                popupWindow.dismiss()
            }

            popupText.text = popupText_interval
            popupTitle.text = popupTitle_interval

            TransitionManager.beginDelayedTransition(activity_main)
            popupWindow.showAtLocation(
                activity_main, // Location to display popup window
                Gravity.BOTTOM, // Exact position of layout to display popup
                0, // X offset
                y_offset // Y offset
            )
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

