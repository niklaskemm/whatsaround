package lbs.whatsaround

import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import lbs.whatsaround.TTS
import com.squareup.picasso.Picasso
import kotlinx.android.synthetic.main.list_item.view.*

class MainAdapter(val homefeed: HomeFeed, val imagelist: ArrayList<String>): RecyclerView.Adapter<CustomViewHolder>()  {

        override fun getItemCount(): Int {
        return homefeed.query.geosearch.count()
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CustomViewHolder {
        val layoutInflater = LayoutInflater.from(parent.context)
        val item = layoutInflater.inflate(R.layout.list_item, parent, false)
        return CustomViewHolder(item)
    }

    override fun onBindViewHolder(holder: CustomViewHolder, position: Int) {
        // Mit .get(position) wird entsprechendes Json Element gewählt
        val wikiArticle = homefeed.query.geosearch.get(position)
        // Gleiches gilt für die imagelist
        val imageUrl = imagelist.get(position)
        val title = wikiArticle.title
        // Set content of textViews
        holder?.view.tv_poiTitle.text = wikiArticle.title
        holder?.view.tv_poiDistance.text = wikiArticle.dist.toString() + "m"
        // Image
        var thumbnailImageView = holder?.view.iv_TitleImage
        Picasso.get().load(imageUrl).into(thumbnailImageView)
        holder.title = title

        // Text To Speech
        //val paragraph = paragraphList.get(position)
        holder?.view.speakButton.setOnClickListener {
            TTS(MainActivity.getContext(), wikiArticle.title,
                "Speak")
        }
        // Stop Text To Speech
        holder?.view.stopButton.setOnClickListener {
            TTS(MainActivity.getContext(), "test", "Stop")
        }

        // Link to Wikipedia
        holder?.view.wikiButton.setOnClickListener {
            val intent = Intent(holder?.view.context, ThirdActivity::class.java)

            intent.putExtra("wiki_title", title)

            holder?.view.context.startActivity(intent)
        }
    }
}

class CustomViewHolder(val view: View, var title: String? = null): RecyclerView.ViewHolder(view)
