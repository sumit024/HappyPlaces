package com.app_devs.happyplaces.activities

import android.app.Activity
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.activity.result.contract.ActivityResultContracts
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.app_devs.happyplaces.R
import com.app_devs.happyplaces.adapters.HappyPlaceAdapter
import com.app_devs.happyplaces.databases.DatabaseHandler
import com.app_devs.happyplaces.models.HappyPlaceModel
import com.app_devs.happyplaces.utils.SwipeToEditCallback
import com.happyplaces.utils.SwipeToDeleteCallback
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
        myAdapter.setOnClickListener(object:HappyPlaceAdapter.OnClickListener{
            override fun onClick(position: Int, model: HappyPlaceModel) {
                val intent=Intent(this@MainActivity,HappyPlaceDetailActivity::class.java)
                //to pass object in intent we make HappyPlaceModel serializable
                intent.putExtra(EXTRA_PLACE_DETAILS,model)
                startActivity(intent)
            }
        })

        val editSwipeHandler=object : SwipeToEditCallback(this)
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=rv_happy_places_list.adapter as HappyPlaceAdapter
                adapter.notifyEditItem(this@MainActivity,viewHolder.adapterPosition, ADD_PLACE_ACTIVITY_REQUEST_CODE)

            }

        }
        val editItemTouchHelper=ItemTouchHelper(editSwipeHandler)
        editItemTouchHelper.attachToRecyclerView(rv_happy_places_list)

        val deleteSwipeHandler=object : SwipeToDeleteCallback(this)
        {
            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val adapter=rv_happy_places_list.adapter as HappyPlaceAdapter
                adapter.deleteItem(viewHolder.adapterPosition)
                //check database again
                getHappyPlacesList()

            }

        }
        val deleteItemTouchHelper=ItemTouchHelper(deleteSwipeHandler)
        deleteItemTouchHelper.attachToRecyclerView(rv_happy_places_list)

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
    companion object
    {
       var EXTRA_PLACE_DETAILS="extra_place_details"
        const val ADD_PLACE_ACTIVITY_REQUEST_CODE = 1
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == ADD_PLACE_ACTIVITY_REQUEST_CODE) {
            if (resultCode == Activity.RESULT_OK) {
               getHappyPlacesList()
            }else{
                Log.e("Activity", "Cancelled or Back Pressed")
            }
        }
    }
}