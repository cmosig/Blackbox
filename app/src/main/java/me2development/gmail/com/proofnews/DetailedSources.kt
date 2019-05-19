package me2development.gmail.com.proofnews

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.beust.klaxon.Klaxon
import kotlinx.android.synthetic.main.activity_detailed_sources.*
import org.json.JSONObject

class DetailedSources : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_detailed_sources)



        txt.text = JSONObject(json.toString()).toString(2)
    }
}
