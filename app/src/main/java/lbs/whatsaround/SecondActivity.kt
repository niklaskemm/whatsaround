package lbs.whatsaround

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.graphics.ColorSpace
import android.location.Location
import android.os.Build
import android.os.Bundle
import android.transition.Fade
import android.transition.TransitionManager
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.widget.ImageView
import android.widget.PopupWindow
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.common.api.ResolvableApiException
import com.google.android.gms.location.*
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.tasks.RuntimeExecutionException
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_second.*
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException
import com.google.gson.JsonParser
import kotlinx.android.synthetic.main.activity_main.*


class SecondActivity : AppCompatActivity() {
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap

    var fusedLocationClient: FusedLocationProviderClient? = null

    val PERMISSION_ID = 42

    private fun checkPermission(vararg perm: String): Boolean {
        val havePermissions = perm.toList().all {
            ContextCompat.checkSelfPermission(this, it) ==
                    PackageManager.PERMISSION_GRANTED
        }
        if (!havePermissions) {
            if (perm.toList().any {
                    ActivityCompat.shouldShowRequestPermissionRationale(this, it)
                }
            ) {
                val dialog = AlertDialog.Builder(this)
                    .setTitle("Permission")
                    .setMessage("Permission needed!")
                    .setPositiveButton("OK") { _, _ ->
                        ActivityCompat.requestPermissions(
                            this, perm, PERMISSION_ID
                        )
                    }
                    .setNegativeButton("No", { _, _ -> })
                    .create()
                dialog.show()
            } else {
                ActivityCompat.requestPermissions(this, perm, PERMISSION_ID)
            }
            return false
        }
        return true
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        recView_ListPOIs.layoutManager = LinearLayoutManager(this)

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (checkPermission(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        ) {
            fusedLocationClient?.lastLocation?.addOnSuccessListener(
                this
            ) { location: Location? ->
                // Got last known location. In some rare
                // situations this can be null.
                if (location == null) {
                    // TODO, handle it
                } else location.apply {
                    // Handle location object
                    fusedLocationClient?.lastLocation!!.addOnSuccessListener { location: Location? ->
                    }
                }
            }
        }

        val reqSetting = LocationRequest.create().apply {
            fastestInterval = intent.getStringExtra("interval").toLong()
            interval = intent.getStringExtra("interval").toLong()
            priority = LocationRequest.PRIORITY_HIGH_ACCURACY
            smallestDisplacement = intent.getStringExtra("smallestDisp").toFloat()
        }

        val REQUEST_CHECK_STATE = 12300 // any suitable ID
        val builder = LocationSettingsRequest.Builder().addLocationRequest(reqSetting)

        val client = LocationServices.getSettingsClient(this)
        client.checkLocationSettings(builder.build()).addOnCompleteListener { _ ->
            try {
            } catch (e: RuntimeExecutionException) {
                if (e.cause is ResolvableApiException)
                    (e.cause as ResolvableApiException).startResolutionForResult(
                        this@SecondActivity,
                        REQUEST_CHECK_STATE
                    )
            }
        }

        val locationUpdates = object : LocationCallback() {
            override fun onLocationResult(lr: LocationResult) {
                // do something with the new location...
                fetchJSON(lr.lastLocation.latitude, lr.lastLocation.longitude)

                mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
                mapFragment.getMapAsync {
                    googleMap = it
                    googleMap.getUiSettings().setZoomControlsEnabled(true)
                    googleMap.animateCamera(
                        CameraUpdateFactory.newLatLngZoom(
                            LatLng(
                                lr.lastLocation.latitude,
                                lr.lastLocation.longitude
                            ), 15f
                        )
                    )
                }
            }
        }

        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            googleMap.isMyLocationEnabled = true
            googleMap.uiSettings.isZoomControlsEnabled = true
        }

        fusedLocationClient?.requestLocationUpdates(
            reqSetting,
            locationUpdates, null /* Looper */
        )
    }

    fun zoomToPosition(lat:Double, lon: Double){
        googleMap.animateCamera(
            CameraUpdateFactory.newLatLngZoom(
                LatLng(lat,lon), 15f))
    }

    fun addMarker(title: String, lat: Double, lon: Double) {
        val position = LatLng(lat, lon)
        googleMap.addMarker(MarkerOptions().title(title).position(position))
    }

    fun fetchJSON(lat: Double, lon: Double) {
        val radius = intent.getStringExtra("radius")
        val limit = intent.getStringExtra("limit")

        val url =
            "https://de.wikipedia.org/w/api.php?origin=*&action=query&list=geosearch&gscoord=$lat|$lon&gsradius=$radius&gslimit=$limit&format=json"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object : Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()

                val gson = GsonBuilder().create()

                val homeFeed = gson.fromJson(body, HomeFeed::class.java)

                // Execute wikiImageScrape() function
                val imageList = wikiImageScrape(homeFeed)

                // Execute wikiParagraphScrape() function
                val paragraphList = wikiParagraphScrape(homeFeed)

                // Wenn nicht innerhalb runOnUiThread{} kann App nicht gestartet werden, wegen Netzwerk Error
                runOnUiThread {
                    // MainAdapter ist für Inhalt UI zuständig. homeFeed = Api results, imageList = Liste mit Imagelinks zu den entsprechenden Articles
                    recView_ListPOIs.adapter = MainAdapter(homeFeed, imageList, paragraphList)
                }

                val markerSetup = listAddMarker(homeFeed)
                runOnUiThread{
                    for (i in 0..(markerSetup.size/3-1)) { //
                        addMarker(markerSetup.get(i*3+0), markerSetup.get(i*3+1).toDouble(), markerSetup.get(i*3+2).toDouble())
                    }
                }
            }

            override fun onFailure(call: Call?, e: IOException?) {
                println("Failed to execute response")
            }

        })



    }

    @Throws(Exception::class)
    fun wikiImageScrape(homeFeed: HomeFeed): ArrayList<String> {
        val wikiTitleImages = ArrayList<String>()
        val geosearchElement = homeFeed.query.geosearch
        try {
            for (i in geosearchElement) {
                val site = "https://de.wikipedia.org/wiki/" + i.title
                val document = Jsoup.connect(site).get()
                val image = document.select("a.image img").first()
                if (image == null) {
                    wikiTitleImages.add("No image found")
                } else {
                    val url = image.attr("abs:src")
                    wikiTitleImages.add(url)
                }
            }
            return wikiTitleImages
        } catch (ex: Exception) {
            return wikiTitleImages
        }

    }

    @Throws(Exception::class)
    fun wikiParagraphScrape(homeFeed: HomeFeed): List<String> {
        val wikiParagraphList = ArrayList<String>()
        val geosearchElement = homeFeed.query.geosearch
        try {
            for (i in geosearchElement) {
                val url =
                    "https://de.wikipedia.org/w/api.php?format=json&action=query&prop=extracts&exintro&explaintext&redirects=1&titles=" + i.title
                val body = Jsoup.connect(url).ignoreContentType(true).execute().body()
                val parser = JsonParser()
                val element = parser.parse(body)
                val obj = element.getAsJsonObject() //since you know it's a JsonObject
                val query = obj.get("query")
                val queryObject = query.asJsonObject
                val pages = queryObject.get("pages")
                val pagesObject = pages.asJsonObject

                val entries = pagesObject.entrySet()//will return members of your object
                for (entry in entries) {
                    val pageid = entry.value
                    val pageidObject = pageid.asJsonObject
                    val title = pageidObject.get("title").toString()
                    val extract = pageidObject.get("extract").toString()
                    wikiParagraphList.add(extract)
                }
            }
            return wikiParagraphList
        } catch (ex: Exception) {
            return wikiParagraphList
        }
    }

    fun listAddMarker(homeFeed: HomeFeed): ArrayList<String> {
        val geosearchElement = homeFeed.query.geosearch
        val markerSetup = ArrayList<String>()
        for (i in geosearchElement) {
            markerSetup.add(i.title)
            markerSetup.add(i.lat.toString())
            markerSetup.add(i.lon.toString())
        }
        return markerSetup
    }
}



