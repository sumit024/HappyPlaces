package com.app_devs.happyplaces.activities

import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.app_devs.happyplaces.R
import com.app_devs.happyplaces.adapters.HappyPlaceAdapter
import com.app_devs.happyplaces.models.HappyPlaceModel
import kotlinx.android.synthetic.main.activity_happy_place_detail.*

class HappyPlaceDetailActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_happy_place_detail)

        var happyPlaceDetailModel: HappyPlaceModel?=null
        if(intent.hasExtra(MainActivity.EXTRA_PLACE_DETAILS))
        {
            happyPlaceDetailModel= intent.getParcelableExtra(MainActivity.EXTRA_PLACE_DETAILS)

        }
        if(happyPlaceDetailModel !=null)
        {
            setSupportActionBar(toolbarHappyPlace)
            supportActionBar?.setDisplayHomeAsUpEnabled(true)
            supportActionBar?.title =happyPlaceDetailModel.title

            toolbarHappyPlace.setNavigationOnClickListener {
                onBackPressed()
            }

            iv_place_image.setImageURI(Uri.parse(happyPlaceDetailModel.image))
            tv_description.text=happyPlaceDetailModel.description
            tv_location.text=happyPlaceDetailModel.location

            btn_view_on_map.setOnClickListener {
                val intent=Intent(this,MapActivity::class.java)
                intent.putExtra(MainActivity.EXTRA_PLACE_DETAILS,happyPlaceDetailModel)
                startActivity(intent)
            }

        }

    }
}