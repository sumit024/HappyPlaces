package com.app_devs.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.LinearLayoutManager
import com.app_devs.happyplaces.R
import com.app_devs.happyplaces.adapters.HappyPlaceAdapter
import com.app_devs.happyplaces.databases.DatabaseHandler
import com.app_devs.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        addHappyPlaces.setOnClickListener {
            val intent= Intent(this, AddHappyPlacesActivity::class.java)
            launcher.launch(intent)
        }
        getHappyPlacesList()

    }

    private fun setUpRecyclerView(list:ArrayList<HappyPlaceModel>)
    {
        rv_happy_places_list.layoutManager=LinearLayoutManager(this)
        rv_happy_places_list.setHasFixedSize(true)
        val myAdapter=HappyPlaceAdapter(this,list)
        rv_happy_places_list.adapter=myAdapter
    }
    private fun getHappyPlacesList()
    {
        val dbHandler=DatabaseHandler(this)
        val getHappyPlaceList=dbHandler.getHappyPlaceList()
        if(getHappyPlaceList.size>0)
        {
           rv_happy_places_list.visibility=View.VISIBLE
            tv_no_records_available.visibility=View.GONE
            setUpRecyclerView(getHappyPlaceList)
        }
        else{
            rv_happy_places_list.visibility=View.GONE
            tv_no_records_available.visibility=View.VISIBLE
        }
    }
    private val launcher=registerForActivityResult(ActivityResultContracts.StartActivityForResult())
    {
        result->

        if(result.resultCode==Activity.RESULT_OK)
        {
            getHappyPlacesList()
        }
        else
            Log.e("ACTIVITY","CANCELLED OR BACK PRESSED")
    }
}