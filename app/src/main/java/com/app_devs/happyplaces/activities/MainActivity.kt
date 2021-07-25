package com.app_devs.happyplaces.activities

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import com.app_devs.happyplaces.R
import com.app_devs.happyplaces.databases.DatabaseHandler
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addHappyPlaces.setOnClickListener {
            val intent= Intent(this, AddHappyPlacesActivity::class.java)
            startActivity(intent)
        }
        getHappyPlacesList()

    }
    private fun getHappyPlacesList()
    {
        val dbHandler=DatabaseHandler(this)
        val getHappyPlaceList=dbHandler.getHappyPlaceList()
        if(getHappyPlaceList.size>0)
        {
            for(i in getHappyPlaceList)
            {
                Log.e("title",i.title)
                Log.e("desc",i.description)

            }
        }
    }
}