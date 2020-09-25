package com.example.listofevents

import android.content.Context
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.ListView
import android.widget.TextView
import com.google.gson.GsonBuilder
import okhttp3.*
import java.io.IOException

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val listView = findViewById<ListView>(R.id.main_listview)
        listView.setBackgroundColor(Color.parseColor("#bfabc13a"))

        fetchJson(listView)
    }

    fun fetchJson(listView: ListView) {
        println("Attempting to fetch JSON")
        val url = "https://api.hackillinois.org/event/"
        val request = Request.Builder().url(url).build()
        val client = OkHttpClient()
        client.newCall(request).enqueue(object: Callback {
            override fun onResponse(call: Call, response: Response) {
                val body = response?.body()?.string()
                println(body)

                val gson = GsonBuilder().create()
                val eventFeed = gson.fromJson(body, EventFeed::class.java)

                runOnUiThread {
                    println("here")
                    listView.adapter = EventAdapter(eventFeed, this@MainActivity)
                    println("andhere")
                }
            }

            override fun onFailure(call: Call, e: IOException) {
                println("Fuck")
            }
        })
    }

    class EventFeed(val events: List<Thing>)
    class Thing(val id: String, val name: String, val description: String, val startTime: Int)

    private class EventAdapter(val eventFeed: EventFeed, context: Context) : BaseAdapter() {

        private val ctext = context

        private val names = arrayListOf<String>("Event 1", "Super Cool Event", "12 event")
        private val items = eventFeed.events

        // number of rows in list
        override fun getCount(): Int {
            return items.size
        }

        override fun getItemId(position: Int): Long {
            return position.toLong()
        }

        override fun getItem(position: Int): Any {
            return "Test String"
        }

        // render rows
        override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
            /* val textView = TextView(ctext)
            textView.text = "Hello World"
            return textView */
            val layoutInflator = LayoutInflater.from(ctext)
            val row = layoutInflator.inflate(R.layout.row, parent, false)
            val locationTextView = row.findViewById<TextView>(R.id.location_textview)
            val nameTextView = row.findViewById<TextView>(R.id.name_textview)
            val descriptionTextView = row.findViewById<TextView>(R.id.description_textview)
            locationTextView.text = "No Location / Online"
            nameTextView.text = items[position].name
            descriptionTextView.text = items[position].description
            return row
        }
    }
}