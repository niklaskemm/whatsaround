package lbs.whatsaround

import android.Manifest
import android.content.pm.PackageManager
import android.location.Location
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.gms.location.FusedLocationProviderClient
import com.google.android.gms.location.LocationCallback
import com.google.android.gms.location.LocationRequest
import com.google.android.gms.location.LocationServices
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.SupportMapFragment
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.GsonBuilder
import kotlinx.android.synthetic.main.activity_second.*
import okhttp3.*
import org.jsoup.Jsoup
import java.io.IOException

class SecondActivity : AppCompatActivity() {
    lateinit var mapFragment: SupportMapFragment
    lateinit var googleMap: GoogleMap

    lateinit var locationRequest: LocationRequest
    lateinit var locationCallback: LocationCallback

    private val MY_PERMISSIONS_REQUEST_FINE_LOCATION = 1

    private lateinit var fusedLocationClient: FusedLocationProviderClient

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray) {

        when (requestCode) {
            MY_PERMISSIONS_REQUEST_FINE_LOCATION -> {
                // If request is cancelled, the result arrays are empty.
                if ((grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED)) {
                    // permission was granted, yay!
                    Toast.makeText(
                        this@SecondActivity,
                        "Accessing GPS Location!",
                        Toast.LENGTH_SHORT
                    ).show()

                } else {
                    // permission denied, boo!
                    Toast.makeText(
                        this@SecondActivity,
                        "Permission denied :(!",
                        Toast.LENGTH_SHORT
                    ).show()
                }
                return
            }

            // Add other 'when' lines to check for other
            // permissions this app might request.
            else -> {
                // Ignore all other requests.
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_second)

        recView_ListPOIs.layoutManager = LinearLayoutManager(this)

        fetchJSON()

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this)

        if (ContextCompat.checkSelfPermission(this@SecondActivity,
                Manifest.permission.ACCESS_FINE_LOCATION)
            != PackageManager.PERMISSION_GRANTED) {

            // Permission is not granted
            // Should we show an explanation?
            if (ActivityCompat.shouldShowRequestPermissionRationale(this@SecondActivity,
                    Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
            } else {
                // No explanation needed, we can request the permission.
                ActivityCompat.requestPermissions(this@SecondActivity,
                    arrayOf(Manifest.permission.ACCESS_FINE_LOCATION),
                    MY_PERMISSIONS_REQUEST_FINE_LOCATION)
            }
        } else {
            fusedLocationClient.lastLocation
                .addOnSuccessListener { location: Location? ->
                    // Permission has already been granted
                }
        }
        mapFragment = supportFragmentManager.findFragmentById(R.id.map) as SupportMapFragment
        mapFragment.getMapAsync {
            googleMap = it
            googleMap.isMyLocationEnabled = true

            val location1 = LatLng(53.5403,10.0048)
            googleMap.addMarker(MarkerOptions().position(location1).title("HafenCity Universität"))
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(location1,15f))

            val locationTest = LatLng(53.550408,9.992411)
            googleMap.addMarker(MarkerOptions().position(locationTest).title("Test Location"))

        }

        fun startLocationUpdates() {
            fusedLocationClient.requestLocationUpdates(locationRequest,
                locationCallback, null /* Looper */)
        }
    }

    fun fetchJSON() {
        val lat = 53.5403
        val lon = 10.0048
        val radius = 10000
        val limit = 30

        val url = "https://de.wikipedia.org/w/api.php?origin=*&action=query&list=geosearch&gscoord=$lat|$lon&gsradius=$radius&gslimit=$limit&format=json"

        val request = Request.Builder().url(url).build()

        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call?, response: Response?) {
                val body = response?.body()?.string()

                val gson = GsonBuilder().create()

                val homeFeed = gson.fromJson(body, HomeFeed::class.java)

                // Execute wikiImageScrape() function
                val imageList = wikiImageScrape(homeFeed)

                // Wenn nicht innerhalb runOnUiThread{} kann App nicht gestartet werden, wegen Netzwerk Error
                runOnUiThread {
                    // MainAdapter ist für Inhalt UI zuständig. homeFeed = Api results, imageList = Liste mit Imagelinks zu den entsprechenden Articles
                    recView_ListPOIs.adapter = MainAdapter(homeFeed, imageList)
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
            var count = 0
            for (i in geosearchElement) {
                count += 1
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
            println(wikiTitleImages)
            return wikiTitleImages
        } catch (ex: Exception) {
            return wikiTitleImages
        }

    }
}

