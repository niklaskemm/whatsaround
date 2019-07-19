package lbs.whatsaround

import android.os.Bundle
import android.webkit.WebViewClient
import androidx.appcompat.app.AppCompatActivity
import kotlinx.android.synthetic.main.activity_third.*

class ThirdActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_third)

        val name = intent.getStringExtra("wiki_title")

        webview.webViewClient = WebViewClient()
        webview.settings.javaScriptEnabled = true
        // webview.settings.loadWithOverviewMode = true
        // webview.settings.useWideViewPort = true

        webview.loadUrl("https://de.wikipedia.org/wiki/" + name)
    }
}
